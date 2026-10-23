import sys
import time
import unicodedata
from functools import lru_cache


# ==========================================
# CONFIGURAÇÃO DA GRADE
# ==========================================

SEMANA = [
    "Segunda-feira",
    "Terça-feira",
    "Quarta-feira",
    "Quinta-feira",
    "Sexta-feira"
]

TURNOS = {
    1: "Manhã",
    2: "Tarde",
    3: "Noite"
}

SLOTS_TURNO = {
    "Manhã": [
        "07h20 - 08h10",
        "08h10 - 09h00",
        "09h00 - 09h50",
        "10h05 - 10h55",
        "10h55 - 11h45",
        "11h45 - 12h35"
    ],
    "Tarde": [
        "13h30 - 14h20",
        "14h20 - 15h10"
    ],
    "Noite": [
        "18h10 - 19h00",
        "19h00 - 19h50",
        "19h50 - 20h40",
        "21h05 - 21h55",
        "21h55 - 22h45"
    ]
}

SLOTS_INTERVALO = {
    "09h50 - 10h05",
    "12h35 - 13h30",
    "15h10 - 18h10",
    "20h40 - 21h05"
}

TODOS_SLOTS = (
    SLOTS_TURNO["Manhã"]
    + SLOTS_TURNO["Tarde"]
    + SLOTS_TURNO["Noite"]
)

SLOTS_GRADE = [
    ("07h20 - 08h10", "aula", "Manhã"),
    ("08h10 - 09h00", "aula", "Manhã"),
    ("09h00 - 09h50", "aula", "Manhã"),
    ("09h50 - 10h05", "intervalo", "Manhã"),
    ("10h05 - 10h55", "aula", "Manhã"),
    ("10h55 - 11h45", "aula", "Manhã"),
    ("11h45 - 12h35", "aula", "Manhã"),
    ("12h35 - 13h30", "intervalo", "Tarde"),
    ("13h30 - 14h20", "aula", "Tarde"),
    ("14h20 - 15h10", "aula", "Tarde"),
    ("15h10 - 18h10", "intervalo", "Tarde"),
    ("18h10 - 19h00", "aula", "Noite"),
    ("19h00 - 19h50", "aula", "Noite"),
    ("19h50 - 20h40", "aula", "Noite"),
    ("20h40 - 21h05", "intervalo", "Noite"),
    ("21h05 - 21h55", "aula", "Noite"),
    ("21h55 - 22h45", "aula", "Noite")
]

ORDEM_GRADE = [horario for horario, _, _ in SLOTS_GRADE]

INTERSTICIO = 11 * 60

LIMITE_SEGUNDOS = 60

ERROS_COM_AJUSTE = {"pouca_disponibilidade", "conflito"}


# ==========================================
# FUNÇÕES BÁSICAS
# ==========================================

def normalizar(texto):
    texto = unicodedata.normalize("NFD", texto)
    texto = "".join(
        caractere
        for caractere in texto
        if unicodedata.category(caractere) != "Mn"
    )
    return " ".join(texto.lower().strip().split())


@lru_cache(maxsize=None)
def inicio_minutos(horario):
    hora, minuto = horario.split(" - ")[0].replace("h", ":").split(":")
    return int(hora) * 60 + int(minuto)


@lru_cache(maxsize=None)
def fim_minutos(horario):
    hora, minuto = horario.split(" - ")[1].replace("h", ":").split(":")
    return int(hora) * 60 + int(minuto)


@lru_cache(maxsize=None)
def turno_do_slot(horario):
    for turno, horarios in SLOTS_TURNO.items():
        if horario in horarios:
            return turno
    return None


@lru_cache(maxsize=None)
def slot_adjacente(slot_a, slot_b):
    if slot_a not in TODOS_SLOTS or slot_b not in TODOS_SLOTS:
        return False

    a = ORDEM_GRADE.index(slot_a)
    b = ORDEM_GRADE.index(slot_b)

    return abs(a - b) == 1


VIZINHOS = {
    horario: [
        outro_horario
        for outro_horario in TODOS_SLOTS
        if slot_adjacente(horario, outro_horario)
    ]
    for horario in TODOS_SLOTS
}


# ==========================================
# REGRAS DE DISPONIBILIDADE
# ==========================================

def turma_pode_usar_slot(turma, horario):
    turno = turno_do_slot(horario)
    return turno in turma["turnos"]


def professor_tem_disponibilidade_strita(professor, dia, horario):
    return horario in professor["disponibilidade"].get(dia, set())


def professor_tem_disponibilidade_ajustada(professor, dia, horario):
    if professor_tem_disponibilidade_strita(professor, dia, horario):
        return False

    turno = turno_do_slot(horario)

    if turno is None:
        return False

    horarios_dia = professor["disponibilidade"].get(dia, set())
    return any(turno_do_slot(slot) == turno for slot in horarios_dia)


def professor_respeita_intersticio(professor_id, dia, horario, agendas):
    agenda = agendas[professor_id]
    inicio_atual = inicio_minutos(horario)
    fim_atual = fim_minutos(horario)
    indice = SEMANA.index(dia)

    # Não pode haver descanso insuficiente com qualquer aula do dia anterior.
    if indice > 0:
        dia_anterior = SEMANA[indice - 1]

        for (dia_ag, slot_ag) in agenda:
            if dia_ag != dia_anterior:
                continue

            descanso = (24 * 60 - fim_minutos(slot_ag)) + inicio_atual

            if descanso < INTERSTICIO:
                return False

    # Nem com qualquer aula do dia seguinte.
    if indice < len(SEMANA) - 1:
        dia_seguinte = SEMANA[indice + 1]

        for (dia_ag, slot_ag) in agenda:
            if dia_ag != dia_seguinte:
                continue

            descanso = (24 * 60 - fim_atual) + inicio_minutos(slot_ag)

            if descanso < INTERSTICIO:
                return False

    return True


def professor_respeita_carga(professor, agendas):
    carga_maxima = professor.get("carga_maxima")

    if not carga_maxima:
        return True

    return len(agendas[professor["id"]]) < carga_maxima


def professor_livre(professor, dia, horario, agendas):
    chave = (dia, horario)
    return (
        chave not in agendas[professor["id"]]
        and professor_respeita_carga(professor, agendas)
        and professor_respeita_intersticio(
            professor["id"],
            dia,
            horario,
            agendas
        )
    )


def professor_para_disciplina(professores, disciplina):
    return [
        professor
        for professor in professores
        if disciplina in professor["disciplinas"]
    ]


# ==========================================
# MOTOR AUTOMÁTICO
# ==========================================

class Agendador:

    def __init__(self, professores, turmas, divisoes, ajuste=False, dias=None):
        self.professores = professores
        self.turmas = turmas
        self.divisoes = divisoes
        self.ajuste = ajuste
        self.dias = [dia for dia in SEMANA if not dias or dia in dias]

        self.agendas_professores = {
            professor["id"]: {}
            for professor in professores
        }

        self.agendas_turmas = {
            turma["chave"]: {}
            for turma in turmas
        }

        self.atribuicoes = {
            turma["chave"]: {}
            for turma in turmas
        }

        self.melhor_atribuicoes = {}
        self.melhor_agendas_professores = {}
        self.melhor_agendas_turmas = {}
        self.melhor_total = -1
        self.nos = 0
        self.limite_nos = 180000 if ajuste else 100000
        self.limite_segundos = LIMITE_SEGUNDOS
        self.tempo_esgotado = False
        self.aptos = {}

    def professores_aptos(self, disciplina):
        if disciplina not in self.aptos:
            self.aptos[disciplina] = professor_para_disciplina(
                self.professores,
                disciplina
            )

        return self.aptos[disciplina]

    def slots_livres(self, turma):
        agenda_turma = self.agendas_turmas[turma["chave"]]

        return [
            (dia, horario)
            for dia in self.dias
            for horario in TODOS_SLOTS
            if (dia, horario) not in agenda_turma
            and turma_pode_usar_slot(turma, horario)
        ]

    def disponibilidade_professor(self, professor, dia, horario):
        if professor_tem_disponibilidade_strita(
            professor,
            dia,
            horario
        ):
            return True, False

        if self.ajuste and professor_tem_disponibilidade_ajustada(
            professor,
            dia,
            horario
        ):
            return True, True

        return False, False

    def candidatos_normais(
        self,
        turma,
        disciplina,
        restantes,
        pontuar=True,
        limite=None
    ):
        candidatos = []
        slots_livres = self.slots_livres(turma)

        for professor in self.professores_aptos(disciplina):
            for dia, horario in slots_livres:
                disponivel, ajuste = self.disponibilidade_professor(
                    professor,
                    dia,
                    horario
                )

                if not disponivel:
                    continue

                if not professor_livre(
                    professor,
                    dia,
                    horario,
                    self.agendas_professores
                ):
                    continue

                score = 0

                if pontuar:
                    # Dá preferência para formar aula dupla automaticamente.
                    for outro_horario in VIZINHOS[horario]:
                        outro = self.atribuicoes[turma["chave"]].get(
                            (dia, outro_horario)
                        )

                        if (
                            outro
                            and outro["tipo"] == "normal"
                            and outro["disciplina"] == disciplina
                            and outro["professor_id"] == professor["id"]
                        ):
                            score += 1000

                    # Evita concentração excessiva da mesma matéria no mesmo dia.
                    quantidade_no_dia = sum(
                        1
                        for (dia_ag, _), registro in self.atribuicoes[turma["chave"]].items()
                        if dia_ag == dia
                        and registro.get("tipo") == "normal"
                        and registro.get("disciplina") == disciplina
                    )

                    score -= quantidade_no_dia * 100

                    # Professores com menor carga têm prioridade.
                    score -= len(
                        self.agendas_professores[professor["id"]]
                    ) * 2

                    # Ajustes recebem penalidade, para serem usados só quando necessário.
                    if ajuste:
                        score -= 600

                    # Evita concentrar aulas somente no começo da semana.
                    score -= SEMANA.index(dia) * 3

                candidatos.append({
                    "tipo": "normal",
                    "dia": dia,
                    "horario": horario,
                    "professor": professor,
                    "disciplina": disciplina,
                    "ajuste": ajuste,
                    "score": score
                })

                if limite is not None and len(candidatos) > limite:
                    return candidatos

        if pontuar:
            candidatos.sort(
                key=lambda candidato: candidato["score"],
                reverse=True
            )

        return candidatos

    def candidatos_divididos(
        self,
        turma,
        disciplina,
        restantes
    ):
        candidatos = []

        for divisao in self.divisoes:
            if divisao["turma"] != turma["chave"]:
                continue

            a = divisao["disciplina_a"]
            b = divisao["disciplina_b"]

            if disciplina == a:
                outra = b
            elif disciplina == b:
                outra = a
            else:
                continue

            if restantes.get(outra, 0) <= 0:
                continue

            professores_a = self.professores_aptos(a)
            professores_b = self.professores_aptos(b)

            for dia, horario in self.slots_livres(turma):
                for professor_a in professores_a:
                    disponivel_a, ajuste_a = self.disponibilidade_professor(
                        professor_a,
                        dia,
                        horario
                    )

                    if not disponivel_a:
                        continue

                    if not professor_livre(
                        professor_a,
                        dia,
                        horario,
                        self.agendas_professores
                    ):
                        continue

                    for professor_b in professores_b:
                        if professor_a["id"] == professor_b["id"]:
                            continue

                        disponivel_b, ajuste_b = self.disponibilidade_professor(
                            professor_b,
                            dia,
                            horario
                        )

                        if not disponivel_b:
                            continue

                        if not professor_livre(
                            professor_b,
                            dia,
                            horario,
                            self.agendas_professores
                        ):
                            continue

                        ajuste = ajuste_a or ajuste_b

                        score = 300
                        score -= (
                            len(self.agendas_professores[professor_a["id"]])
                            + len(self.agendas_professores[professor_b["id"]])
                        ) * 2

                        if ajuste:
                            score -= 600

                        candidatos.append({
                            "tipo": "dividida",
                            "dia": dia,
                            "horario": horario,
                            "professor_a": professor_a,
                            "professor_b": professor_b,
                            "disciplina_a": a,
                            "disciplina_b": b,
                            "ajuste": ajuste,
                            "score": score
                        })

        candidatos.sort(
            key=lambda candidato: candidato["score"],
            reverse=True
        )

        return candidatos

    def listar_candidatos(self, turma, disciplina, restantes):
        return (
            self.candidatos_normais(
                turma,
                disciplina,
                restantes
            )
            + self.candidatos_divididos(
                turma,
                disciplina,
                restantes
            )
        )

    def contar_candidatos(self, turma, disciplina, restantes, limite=None):
        quantidade = len(self.candidatos_normais(
            turma,
            disciplina,
            restantes,
            pontuar=False,
            limite=limite
        ))

        if limite is not None and quantidade > limite:
            return quantidade

        return quantidade + len(self.candidatos_divididos(
            turma,
            disciplina,
            restantes
        ))

    def contar_restantes(self, restantes_geral):
        return sum(
            max(0, quantidade)
            for quantidade in restantes_geral.values()
        )

    def registrar_melhor_estado(self, restantes_geral, pulados):
        restantes_total = self.contar_restantes(restantes_geral) + pulados

        total_atual = self.total_aulas - restantes_total

        if total_atual <= self.melhor_total:
            return

        self.melhor_total = total_atual

        self.melhor_atribuicoes = {
            turma_chave: dict(registros)
            for turma_chave, registros in self.atribuicoes.items()
        }

        self.melhor_agendas_professores = {
            professor_id: dict(agenda)
            for professor_id, agenda in self.agendas_professores.items()
        }

        self.melhor_agendas_turmas = {
            turma_chave: dict(agenda)
            for turma_chave, agenda in self.agendas_turmas.items()
        }

    def aplicar_normal(self, turma, candidato):
        chave = (candidato["dia"], candidato["horario"])
        turma_chave = turma["chave"]
        professor = candidato["professor"]

        self.agendas_turmas[turma_chave][chave] = True
        self.agendas_professores[professor["id"]][chave] = turma_chave

        self.atribuicoes[turma_chave][chave] = {
            "tipo": "normal",
            "professor_id": professor["id"],
            "professor": professor["nome"],
            "disciplina": candidato["disciplina"],
            "disciplina_nome": professor["disciplinas"].get(
                candidato["disciplina"],
                candidato["disciplina"]
            ),
            "ajuste": candidato.get("ajuste", False)
        }

    def desfazer_normal(self, turma, candidato):
        chave = (candidato["dia"], candidato["horario"])
        turma_chave = turma["chave"]
        professor = candidato["professor"]

        self.agendas_turmas[turma_chave].pop(chave, None)
        self.agendas_professores[professor["id"]].pop(chave, None)
        self.atribuicoes[turma_chave].pop(chave, None)

    def aplicar_dividida(self, turma, candidato):
        chave = (candidato["dia"], candidato["horario"])
        turma_chave = turma["chave"]
        professor_a = candidato["professor_a"]
        professor_b = candidato["professor_b"]

        self.agendas_turmas[turma_chave][chave] = True
        self.agendas_professores[professor_a["id"]][chave] = turma_chave
        self.agendas_professores[professor_b["id"]][chave] = turma_chave

        self.atribuicoes[turma_chave][chave] = {
            "tipo": "dividida",
            "professor_a_id": professor_a["id"],
            "professor_a": professor_a["nome"],
            "disciplina_a": candidato["disciplina_a"],
            "disciplina_a_nome": professor_a["disciplinas"].get(
                candidato["disciplina_a"],
                candidato["disciplina_a"]
            ),
            "professor_b_id": professor_b["id"],
            "professor_b": professor_b["nome"],
            "disciplina_b": candidato["disciplina_b"],
            "disciplina_b_nome": professor_b["disciplinas"].get(
                candidato["disciplina_b"],
                candidato["disciplina_b"]
            ),
            "ajuste": candidato.get("ajuste", False)
        }

    def desfazer_dividida(self, turma, candidato):
        chave = (candidato["dia"], candidato["horario"])
        turma_chave = turma["chave"]
        professor_a = candidato["professor_a"]
        professor_b = candidato["professor_b"]

        self.agendas_turmas[turma_chave].pop(chave, None)
        self.agendas_professores[professor_a["id"]].pop(chave, None)
        self.agendas_professores[professor_b["id"]].pop(chave, None)
        self.atribuicoes[turma_chave].pop(chave, None)

    def construir_itens(self):
        itens = []

        for turma in self.turmas:
            for disciplina, dados in turma["disciplinas"].items():
                for numero in range(dados["qtd"]):
                    itens.append({
                        "turma": turma,
                        "disciplina": disciplina,
                        "numero": numero + 1
                    })

        return itens

    def executar(self):
        itens = self.construir_itens()

        self.total_aulas = len(itens)
        self.melhor_total = -1
        self.nos = 0
        self.tempo_esgotado = False
        inicio = time.monotonic()

        sys.setrecursionlimit(max(1000, self.total_aulas * 4 + 200))

        restantes = {}
        for turma in self.turmas:
            for disciplina, dados in turma["disciplinas"].items():
                restantes[(turma["chave"], disciplina)] = dados["qtd"]

        turmas_por_chave = {
            turma["chave"]: turma
            for turma in self.turmas
        }

        def restantes_da_turma(turma_chave):
            return {
                disc: restante
                for (t_chave, disc), restante in restantes.items()
                if t_chave == turma_chave
            }

        def recursao(pulados):
            if self.nos >= self.limite_nos:
                return False

            if time.monotonic() - inicio > self.limite_segundos:
                self.tempo_esgotado = True
                return False

            self.nos += 1
            self.registrar_melhor_estado(restantes, pulados)

            if self.contar_restantes(restantes) == 0:
                return pulados == 0

            melhor = None
            menor_quantidade = None

            for chave, quantidade in restantes.items():
                if quantidade <= 0:
                    continue

                turma_chave, disciplina = chave

                quantidade_candidatos = self.contar_candidatos(
                    turmas_por_chave[turma_chave],
                    disciplina,
                    restantes_da_turma(turma_chave),
                    limite=menor_quantidade
                )

                if melhor is None or quantidade_candidatos < menor_quantidade:
                    melhor = chave
                    menor_quantidade = quantidade_candidatos

                    if quantidade_candidatos <= 1:
                        break

            turma_chave, disciplina = melhor
            turma = turmas_por_chave[turma_chave]

            if menor_quantidade == 0:
                restantes[melhor] -= 1
                resultado = recursao(pulados + 1)
                restantes[melhor] += 1
                return resultado

            candidatos = self.listar_candidatos(
                turma,
                disciplina,
                restantes_da_turma(turma_chave)
            )

            # Coloca primeiro soluções normais; divisão e ajuste ficam depois.
            candidatos.sort(
                key=lambda candidato: (
                    candidato["ajuste"],
                    candidato["tipo"] != "normal",
                    -candidato["score"]
                )
            )

            tentados = set()

            for candidato in candidatos:
                chave_slot = (
                    candidato["tipo"],
                    candidato["dia"],
                    candidato["horario"],
                    candidato.get("professor", {}).get("id")
                    if candidato["tipo"] == "normal"
                    else (
                        candidato["professor_a"]["id"],
                        candidato["professor_b"]["id"]
                    )
                )

                if chave_slot in tentados:
                    continue

                tentados.add(chave_slot)

                if candidato["tipo"] == "normal":
                    self.aplicar_normal(turma, candidato)
                    restantes[(turma_chave, disciplina)] -= 1

                    if recursao(pulados):
                        return True

                    restantes[(turma_chave, disciplina)] += 1
                    self.desfazer_normal(turma, candidato)

                else:
                    a = candidato["disciplina_a"]
                    b = candidato["disciplina_b"]
                    chave_a = (turma_chave, a)
                    chave_b = (turma_chave, b)

                    if restantes.get(chave_a, 0) <= 0 or restantes.get(chave_b, 0) <= 0:
                        continue

                    self.aplicar_dividida(turma, candidato)
                    restantes[chave_a] -= 1
                    restantes[chave_b] -= 1

                    if recursao(pulados):
                        return True

                    restantes[chave_a] += 1
                    restantes[chave_b] += 1
                    self.desfazer_dividida(turma, candidato)

                if self.tempo_esgotado or self.nos >= self.limite_nos:
                    return False

            return False

        completo = recursao(0)

        # Carrega o melhor estado encontrado, mesmo que não seja completo.
        self.atribuicoes = {
            turma_chave: dict(registros)
            for turma_chave, registros in self.melhor_atribuicoes.items()
        }

        self.agendas_professores = {
            professor_id: dict(agenda)
            for professor_id, agenda in self.melhor_agendas_professores.items()
        }

        self.agendas_turmas = {
            turma_chave: dict(agenda)
            for turma_chave, agenda in self.melhor_agendas_turmas.items()
        }

        return completo

    def contar_faltas(self):
        faltas = {}

        for turma in self.turmas:
            faltas[turma["chave"]] = {}
            agenda = self.atribuicoes[turma["chave"]]

            for disciplina, dados in turma["disciplinas"].items():
                feitas = 0

                for registro in agenda.values():
                    if registro["tipo"] == "normal":
                        if registro.get("disciplina") == disciplina:
                            feitas += 1
                    else:
                        if registro.get("disciplina_a") == disciplina:
                            feitas += 1
                        if registro.get("disciplina_b") == disciplina:
                            feitas += 1

                faltas[turma["chave"]][disciplina] = max(
                    0,
                    dados["qtd"] - feitas
                )

        return faltas

    def total_faltas(self):
        return sum(
            quantidade
            for faltas_turma in self.contar_faltas().values()
            for quantidade in faltas_turma.values()
        )

    def listar_ajustes(self):
        ajustes = []

        for turma in self.turmas:
            for (dia, horario), registro in self.atribuicoes[turma["chave"]].items():
                if not registro.get("ajuste"):
                    continue

                if registro["tipo"] == "normal":
                    professores = [(registro["professor"], registro["disciplina_nome"])]
                else:
                    professores = [
                        (registro["professor_a"], registro["disciplina_a_nome"]),
                        (registro["professor_b"], registro["disciplina_b_nome"])
                    ]

                for professor, disciplina in professores:
                    ajustes.append({
                        "turma": turma["nome"],
                        "dia": dia,
                        "horario": horario,
                        "professor": professor,
                        "disciplina": disciplina
                    })

        ajustes.sort(key=lambda item: (SEMANA.index(item["dia"]), ORDEM_GRADE.index(item["horario"])))
        return ajustes

    def marcar_aulas_duplas(self):
        for turma in self.turmas:
            agenda = self.atribuicoes[turma["chave"]]

            for (dia, horario), registro in list(agenda.items()):
                if registro["tipo"] != "normal":
                    continue

                for outro_horario in VIZINHOS[horario]:
                    outro = agenda.get((dia, outro_horario))

                    if not outro or outro["tipo"] != "normal":
                        continue

                    if (
                        outro.get("disciplina") == registro.get("disciplina")
                        and outro.get("professor_id") == registro.get("professor_id")
                    ):
                        registro["aula_dupla"] = True
                        outro["aula_dupla"] = True


def diagnosticar_faltas(professores, turmas, agendador):
    erros = []
    faltas = agendador.contar_faltas()

    for turma in turmas:
        for disciplina, quantidade in faltas[turma["chave"]].items():
            if quantidade <= 0:
                continue

            dados = turma["disciplinas"][disciplina]
            aptos = professor_para_disciplina(professores, disciplina)

            horarios_turma = [
                (dia, horario)
                for dia in agendador.dias
                for horario in TODOS_SLOTS
                if turma_pode_usar_slot(turma, horario)
            ]

            disponiveis = {
                professor["id"]: [
                    chave
                    for chave in horarios_turma
                    if professor_tem_disponibilidade_strita(professor, chave[0], chave[1])
                ]
                for professor in aptos
            }

            total_disponivel = len({
                chave
                for horarios in disponiveis.values()
                for chave in horarios
            })

            if not aptos:
                tipo = "sem_professor"
                motivo = "Nenhum professor cadastrado leciona essa disciplina."
                solucao = f"Vincule a disciplina {dados['nome']} a pelo menos um professor."
            elif total_disponivel == 0:
                tipo = "sem_turno"
                nomes = ", ".join(professor["nome"] for professor in aptos)
                turnos = ", ".join(turma["turnos"])
                motivo = f"Os professores da disciplina ({nomes}) não têm disponibilidade no turno da turma ({turnos})."
                solucao = "Peça para o professor liberar horários nesse turno ou vincule outro professor."
            elif total_disponivel < dados["qtd"]:
                tipo = "pouca_disponibilidade"
                motivo = (
                    f"Os professores têm só {total_disponivel} horário(s) disponível(is) no turno da turma, "
                    f"mas a turma precisa de {dados['qtd']} aula(s)."
                )
                solucao = "Aumente a disponibilidade do professor ou diminua as aulas por semana."
            elif all(
                professor.get("carga_maxima")
                and len(agendador.agendas_professores[professor["id"]]) >= professor["carga_maxima"]
                for professor in aptos
            ):
                tipo = "carga"
                nomes = ", ".join(
                    f"{professor['nome']} ({professor['carga_maxima']} aulas)"
                    for professor in aptos
                )
                motivo = f"A carga horária máxima dos professores foi atingida: {nomes}."
                solucao = "Aumente a carga horária máxima ou vincule outro professor."
            else:
                tipo = "conflito"
                motivo = (
                    "Os horários livres dos professores já foram usados por outras turmas "
                    "ou quebrariam o interstício de 11 horas entre um dia e outro."
                )
                solucao = "Libere mais horários para o professor ou use a grade de solução sugerida."

            erros.append({
                "tipo": tipo,
                "turma": turma["nome"],
                "disciplina": dados["nome"],
                "faltando": quantidade,
                "motivo": motivo,
                "solucao": solucao
            })

    return erros


def montar_grade(professores, turmas, divisoes, dias=None):
    agendador = Agendador(professores, turmas, divisoes, ajuste=False, dias=dias)
    completo = agendador.executar()
    agendador.marcar_aulas_duplas()

    resultado = {
        "agendador": agendador,
        "completo": completo,
        "erros": [],
        "sugestao": None
    }

    if agendador.total_faltas() == 0:
        resultado["completo"] = True
        return resultado

    resultado["completo"] = False
    resultado["erros"] = diagnosticar_faltas(professores, turmas, agendador)

    if not any(erro["tipo"] in ERROS_COM_AJUSTE for erro in resultado["erros"]):
        return resultado

    sugestao = Agendador(professores, turmas, divisoes, ajuste=True, dias=dias)
    sugestao.executar()
    sugestao.marcar_aulas_duplas()

    if sugestao.total_faltas() < agendador.total_faltas():
        resultado["sugestao"] = sugestao

    return resultado
