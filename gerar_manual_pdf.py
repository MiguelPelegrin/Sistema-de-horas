from pathlib import Path
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import PageBreak, Paragraph, Preformatted, SimpleDocTemplate, Spacer

ROOT = Path(__file__).parent
OUTPUT = ROOT / "Manual-Inicializacao-Sistema-de-Horas.pdf"
FONT_DIR = Path(r"C:\Windows\Fonts")

pdfmetrics.registerFont(TTFont("Arial", str(FONT_DIR / "arial.ttf")))
pdfmetrics.registerFont(TTFont("Arial-Bold", str(FONT_DIR / "arialbd.ttf")))

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(
    name="TitleCustom", parent=styles["Title"], fontName="Arial-Bold",
    fontSize=22, leading=28, alignment=TA_CENTER, textColor="#17324D",
    spaceAfter=18,
))
styles.add(ParagraphStyle(
    name="HeadingCustom", parent=styles["Heading2"], fontName="Arial-Bold",
    fontSize=14, leading=18, textColor="#176B87", spaceBefore=12, spaceAfter=7,
))
styles.add(ParagraphStyle(
    name="BodyCustom", parent=styles["BodyText"], fontName="Arial",
    fontSize=10, leading=15, spaceAfter=6,
))
styles.add(ParagraphStyle(
    name="CodeCustom", parent=styles["Code"], fontName="Courier",
    fontSize=8.5, leading=11, leftIndent=8, rightIndent=8,
    backColor="#F1F4F6", borderColor="#D5DDE3", borderWidth=0.5,
    borderPadding=7, spaceBefore=4, spaceAfter=9,
))


def footer(canvas, doc):
    canvas.saveState()
    canvas.setFont("Arial", 8)
    canvas.setFillColorRGB(0.35, 0.4, 0.45)
    canvas.drawString(18 * mm, 12 * mm, "Sistema de Horas - Manual de inicialização")
    canvas.drawRightString(192 * mm, 12 * mm, f"Página {doc.page}")
    canvas.restoreState()


def paragraph(text, style="BodyCustom"):
    return Paragraph(text.replace("&", "&amp;"), styles[style])


story = [
    paragraph("Manual de Inicialização do Sistema de Horas", "TitleCustom"),
    paragraph("Guia rápido para executar o banco de dados, o backend Spring Boot e o frontend.", "BodyCustom"),
    Spacer(1, 8),
]

content = Path(ROOT / "Manual-Inicializacao.md").read_text(encoding="utf-8").splitlines()
for line in content:
    if line.startswith("# "):
        continue
    if line.startswith("## "):
        story.append(paragraph(line[3:], "HeadingCustom"))
    elif line.startswith("### "):
        story.append(paragraph(line[4:], "HeadingCustom"))
    elif line == "```powershell" or line == "```":
        continue
    elif line.startswith("- "):
        story.append(paragraph("• " + line[2:]))
    elif line[:2].isdigit() and line[2:4] == ". ":
        story.append(paragraph(line))
    elif line.startswith("`") and line.endswith("`"):
        story.append(paragraph(line[1:-1], "CodeCustom"))
    elif line.strip():
        story.append(paragraph(line.replace("`", "")))
    else:
        story.append(Spacer(1, 3))

# Rebuild code blocks from the Markdown source with readable monospace formatting.
raw = Path(ROOT / "Manual-Inicializacao.md").read_text(encoding="utf-8")
blocks = []
inside = False
current = []
for line in raw.splitlines():
    if line.startswith("```"):
        if inside:
            blocks.append("\n".join(current))
            current = []
        inside = not inside
    elif inside:
        current.append(line)

# Add code blocks at the end as a reference appendix, preserving exact commands.
story.append(PageBreak())
story.append(paragraph("Comandos completos", "HeadingCustom"))
for block in blocks:
    story.append(Preformatted(block, styles["CodeCustom"]))

doc = SimpleDocTemplate(
    str(OUTPUT), pagesize=A4, rightMargin=18 * mm, leftMargin=18 * mm,
    topMargin=16 * mm, bottomMargin=20 * mm,
    title="Manual de Inicialização do Sistema de Horas",
)
doc.build(story, onFirstPage=footer, onLaterPages=footer)
print(OUTPUT)
