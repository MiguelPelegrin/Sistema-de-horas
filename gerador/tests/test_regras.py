import os
import sys
import unittest
from io import BytesIO

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from openpyxl import load_workbook

import regras
from planilha import gerar_planilha


def professor(id, nome, disciplinas, disponibilidade, carga_maxima=None):
    return {
        "id": id,
        "nome": nome,
        "disciplinas": {disciplina: disciplina.title() for disciplina in disciplinas},
        "disponibilidade": {dia: set(horarios) for dia, horarios in disponibilidade.items()},
        "carga_maxima": carga_maxima
    }


def turma(chave, turnos, disciplinas, siglas=None):
    return {
        "id": chave,
        "nome": f"Turma {chave}",
        "chave": chave,
        "turnos": turnos,
        "disciplinas": {
            disciplina: {
                "nome": disciplina.title(),
                "sigla": (siglas or {}).get(disciplina),
                "qtd": quantidade
            }
            for disciplina, quantidade in disciplinas.items()
        }
    }


def semana_toda(turno):
    return {dia: regras.SLOTS_TURNO[turno] for dia in regras.SEMANA}


def aulas_do_professor(agendador, professor_id):
    return sorted(
        agendador.agendas_professores[professor_id].keys(),
        key=lambda chave: (regras.SEMANA.index(chave[0]), regras.ORDEM_GRADE.index(chave[1]))
    )


class TestRegras(unittest.TestCase):

    def test_grade_completa_quando_tudo_cabe(self):
        professores = [
            professor(1, "Ana", ["mat"], semana_toda("Manhã")),
            professor(2, "Bruno", ["port"], semana_toda("Manhã"))
        ]
        turmas = [turma("1", ["Manhã"], {"mat": 5, "port": 4})]

        resultado = regras.montar_grade(professores, turmas, [])

        self.assertTrue(resultado["completo"])
        self.assertEqual(resultado["agendador"].total_faltas(), 0)
        self.assertEqual(resultado["erros"], [])

    def test_professor_nunca_fica_em_duas_turmas_ao_mesmo_tempo(self):
        professores = [professor(1, "Ana", ["mat"], semana_toda("Manhã"))]
        turmas = [
            turma("1", ["Manhã"], {"mat": 5}),
            turma("2", ["Manhã"], {"mat": 5})
        ]

        agendador = regras.montar_grade(professores, turmas, [])["agendador"]
        horarios = [
            chave
            for turma_chave in ("1", "2")
            for chave in agendador.atribuicoes[turma_chave]
        ]

        self.assertEqual(len(horarios), 10)
        self.assertEqual(len(horarios), len(set(horarios)))

    def test_intersticio_de_11_horas_entre_noite_e_manha(self):
        disponibilidade = {
            "Segunda-feira": regras.SLOTS_TURNO["Noite"],
            "Terça-feira": regras.SLOTS_TURNO["Manhã"]
        }
        professores = [professor(1, "Junior", ["mat"], disponibilidade)]
        turmas = [
            turma("noite", ["Noite"], {"mat": 2}),
            turma("manha", ["Manhã"], {"mat": 2})
        ]

        agendador = regras.montar_grade(professores, turmas, [])["agendador"]
        aulas = aulas_do_professor(agendador, 1)
        segunda = [horario for dia, horario in aulas if dia == "Segunda-feira"]
        terca = [horario for dia, horario in aulas if dia == "Terça-feira"]

        self.assertTrue(segunda and terca)
        descanso = 24 * 60 - regras.fim_minutos(max(segunda, key=regras.fim_minutos)) \
            + regras.inicio_minutos(min(terca, key=regras.inicio_minutos))
        self.assertGreaterEqual(descanso, regras.INTERSTICIO)

    def test_carga_horaria_maxima_e_respeitada(self):
        professores = [professor(1, "Eva", ["fis"], semana_toda("Manhã"), carga_maxima=3)]
        turmas = [turma("1", ["Manhã"], {"fis": 5})]

        resultado = regras.montar_grade(professores, turmas, [])

        self.assertEqual(len(resultado["agendador"].agendas_professores[1]), 3)
        self.assertEqual(resultado["agendador"].total_faltas(), 2)
        self.assertEqual(resultado["erros"][0]["tipo"], "carga")

    def test_aula_dupla_nao_atravessa_o_intervalo(self):
        self.assertTrue(regras.slot_adjacente("07h20 - 08h10", "08h10 - 09h00"))
        self.assertFalse(regras.slot_adjacente("09h00 - 09h50", "10h05 - 10h55"))
        self.assertFalse(regras.slot_adjacente("14h20 - 15h10", "18h10 - 19h00"))

    def test_dias_letivos_limitam_a_grade(self):
        professores = [professor(1, "Ana", ["mat"], semana_toda("Manhã"))]
        turmas = [turma("1", ["Manhã"], {"mat": 4})]

        agendador = regras.montar_grade(professores, turmas, [], dias=["Segunda-feira", "Terça-feira"])["agendador"]
        dias_usados = {dia for dia, _ in agendador.atribuicoes["1"]}

        self.assertTrue(dias_usados <= {"Segunda-feira", "Terça-feira"})

    def test_disciplina_sem_professor_vira_erro(self):
        professores = [professor(1, "Ana", ["mat"], semana_toda("Manhã"))]
        turmas = [turma("1", ["Manhã"], {"mat": 2, "ingles": 2})]

        resultado = regras.montar_grade(professores, turmas, [])

        self.assertFalse(resultado["completo"])
        self.assertEqual(len(resultado["erros"]), 1)
        self.assertEqual(resultado["erros"][0]["tipo"], "sem_professor")
        self.assertIsNone(resultado["sugestao"])

    def test_solucao_sugerida_quando_falta_disponibilidade(self):
        professores = [professor(1, "Andreia", ["port"], {"Segunda-feira": ["07h20 - 08h10"]})]
        turmas = [turma("1", ["Manhã"], {"port": 3})]

        resultado = regras.montar_grade(professores, turmas, [])

        self.assertEqual(resultado["erros"][0]["tipo"], "pouca_disponibilidade")
        self.assertIsNotNone(resultado["sugestao"])
        self.assertLess(resultado["sugestao"].total_faltas(), resultado["agendador"].total_faltas())
        self.assertTrue(resultado["sugestao"].listar_ajustes())

    def test_aula_dividida_usa_dois_professores_no_mesmo_horario(self):
        dois_horarios = {"Segunda-feira": ["07h20 - 08h10", "08h10 - 09h00"]}
        professores = [
            professor(1, "Carla", ["web"], dois_horarios),
            professor(2, "Davi", ["bd"], dois_horarios)
        ]
        turmas = [turma("1", ["Manhã"], {"web": 2, "bd": 2})]
        divisoes = [{"turma": "1", "disciplina_a": "web", "disciplina_b": "bd"}]

        agendador = regras.montar_grade(professores, turmas, divisoes)["agendador"]
        tipos = [registro["tipo"] for registro in agendador.atribuicoes["1"].values()]

        self.assertEqual(agendador.total_faltas(), 0)
        self.assertEqual(tipos, ["dividida", "dividida"])

    def test_resultado_e_sempre_o_mesmo(self):
        professores = [professor(i, f"P{i}", ["mat", "port"], semana_toda("Manhã")) for i in range(1, 4)]
        turmas = [turma(str(i), ["Manhã"], {"mat": 4, "port": 4}) for i in range(1, 5)]

        primeira = regras.montar_grade(professores, turmas, [])["agendador"].atribuicoes
        segunda = regras.montar_grade(professores, turmas, [])["agendador"].atribuicoes

        self.assertEqual(primeira, segunda)

    def test_sabado_so_entra_quando_e_dia_letivo(self):
        professores = [professor(1, "Ana", ["mat"], {"Sábado": regras.SLOTS_TURNO["Manhã"]})]
        turmas = [turma("1", ["Manhã"], {"mat": 2})]

        sem_sabado = regras.montar_grade(professores, turmas, [])["agendador"]
        com_sabado = regras.montar_grade(professores, turmas, [], dias=regras.SEMANA)["agendador"]

        self.assertEqual(sem_sabado.total_faltas(), 2)
        self.assertEqual(com_sabado.total_faltas(), 0)
        self.assertEqual({dia for dia, _ in com_sabado.atribuicoes["1"]}, {"Sábado"})

    def test_modulo_noturno_usa_blocos_que_valem_duas_aulas(self):
        professores = [professor(1, "Leandro", ["qge"], semana_toda("Módulo"), carga_maxima=4)]
        turmas = [turma("1", ["Módulo"], {"qge": 3})]

        resultado = regras.montar_grade(professores, turmas, [])
        horarios = {horario for _, horario in resultado["agendador"].atribuicoes["1"]}

        self.assertEqual(resultado["agendador"].total_faltas(), 1)
        self.assertTrue(horarios <= set(regras.SLOTS_TURNO["Módulo"]))
        self.assertEqual(resultado["erros"][0]["tipo"], "carga")

    def test_professor_nao_fica_no_modulo_e_na_noite_ao_mesmo_tempo(self):
        disponibilidade = {"Segunda-feira": ["19h00 - 19h50", "19h00 - 20h50"]}
        professores = [professor(1, "Junior", ["apfq"], disponibilidade)]
        turmas = [
            turma("noite", ["Noite"], {"apfq": 1}),
            turma("modulo", ["Módulo"], {"apfq": 1})
        ]

        agendador = regras.montar_grade(professores, turmas, [], dias=["Segunda-feira"])["agendador"]

        self.assertEqual(agendador.total_faltas(), 1)
        self.assertTrue(regras.slots_se_chocam("19h00 - 19h50", "19h00 - 20h50"))
        self.assertFalse(regras.slots_se_chocam("18h10 - 19h00", "19h00 - 20h50"))

    def test_professor_tem_no_maximo_8_aulas_por_dia(self):
        professores = [professor(1, "Rejane", ["ingles"], {
            "Segunda-feira": regras.SLOTS_TURNO["Manhã"] + regras.SLOTS_TURNO["Tarde"] + regras.SLOTS_TURNO["Noite"]
        })]
        turmas = [
            turma("integral", ["Manhã", "Tarde"], {"ingles": 8}),
            turma("noite", ["Noite"], {"ingles": 2})
        ]

        agendador = regras.montar_grade(professores, turmas, [], dias=["Segunda-feira"])["agendador"]

        self.assertEqual(regras.carga_do_professor(agendador.agendas_professores[1], "Segunda-feira"), 8)
        self.assertEqual(agendador.total_faltas(), 2)


class TestPlanilha(unittest.TestCase):

    def test_planilha_com_erros_e_solucao_tem_todas_as_abas(self):
        professores = [professor(1, "Andreia", ["port"], {"Segunda-feira": ["07h20 - 08h10"]})]
        turmas = [turma("1", ["Manhã"], {"port": 3})]
        resultado = regras.montar_grade(professores, turmas, [])

        arquivo = gerar_planilha("ETEC Teste", "Coordenação", turmas, resultado, {"port": "9DC3E6"})
        planilha = load_workbook(BytesIO(arquivo.getvalue()))

        self.assertEqual(planilha.sheetnames, ["Grade", "Erros", "Solução sugerida", "Ajustes da solução"])
        self.assertEqual(planilha["Grade"]["A1"].value, "ETEC Teste")
        self.assertEqual(planilha["Erros"].cell(3, 2).value, "Port")

    def test_planilha_completa_tem_so_a_grade(self):
        professores = [professor(1, "Ana", ["mat"], semana_toda("Manhã"))]
        turmas = [turma("1", ["Manhã"], {"mat": 2})]
        resultado = regras.montar_grade(professores, turmas, [])

        planilha = load_workbook(BytesIO(gerar_planilha("ETEC", None, turmas, resultado, {}).getvalue()))

        self.assertEqual(planilha.sheetnames, ["Grade"])

    def test_aula_mostra_sigla_e_usa_a_cor_do_professor(self):
        professores = [professor(1, "Ana", ["mat"], semana_toda("Manhã"))]
        turmas = [turma("1", ["Manhã"], {"mat": 1}, siglas={"mat": "MAT"})]
        resultado = regras.montar_grade(professores, turmas, [])

        planilha = load_workbook(BytesIO(gerar_planilha("ETEC", None, turmas, resultado, {"1": "1F4E79"}).getvalue()))
        celulas = [
            celula
            for linha in planilha["Grade"].iter_rows()
            for celula in linha
            if celula.value == "MAT\nAna"
        ]

        self.assertEqual(len(celulas), 1)
        self.assertEqual(celulas[0].fill.fgColor.rgb[-6:], "1F4E79")
        self.assertEqual(celulas[0].font.color.rgb[-6:], "FFFFFF")

    def test_modulo_noturno_ganha_aba_propria(self):
        professores = [
            professor(1, "Ana", ["mat"], semana_toda("Manhã")),
            professor(2, "Sidney", ["pti"], semana_toda("Módulo"))
        ]
        turmas = [
            turma("1", ["Manhã"], {"mat": 2}),
            turma("2", ["Módulo"], {"pti": 2})
        ]
        resultado = regras.montar_grade(professores, turmas, [])

        planilha = load_workbook(BytesIO(gerar_planilha("ETEC", None, turmas, resultado, {}).getvalue()))
        horarios_modulo = [linha[1].value for linha in planilha["Grade módulo noturno"].iter_rows(min_row=7, max_row=9)]

        self.assertEqual(planilha.sheetnames, ["Grade", "Grade módulo noturno"])
        self.assertEqual(horarios_modulo, ["19h00 - 20h50", "20h50 - 21h05", "21h05 - 22h50"])

    def test_texto_comecando_com_igual_nao_vira_formula(self):
        professores = [professor(1, "=CMD|' /C calc'!A0", ["mat"], semana_toda("Manhã"))]
        turmas = [turma("1", ["Manhã"], {"mat": 1}, siglas={"mat": "=1+1"})]
        resultado = regras.montar_grade(professores, turmas, [])

        planilha = load_workbook(BytesIO(gerar_planilha('=HYPERLINK("http://x.com")', "=2+2", turmas, resultado, {}).getvalue()))
        formulas = [
            celula.coordinate
            for linha in planilha["Grade"].iter_rows()
            for celula in linha
            if celula.data_type == "f"
        ]

        self.assertEqual(formulas, [])
        self.assertEqual(planilha["Grade"]["A1"].value, '=HYPERLINK("http://x.com")')


if __name__ == "__main__":
    unittest.main()
