from fastapi import FastAPI, File, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from PIL import Image
import pytesseract
import io

app = FastAPI()

# Configuração do CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Permita todas as origens para fins de desenvolvimento
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/api/data")
async def get_data():
    return {"message": "Teste"}

@app.post("/api/extract_text")
async def extract_text(file: UploadFile = File(...)):
    try:
        # Leia a imagem enviada
        image = Image.open(io.BytesIO(await file.read()))
        # Extraia o texto da imagem usando pytesseract
        text = pytesseract.image_to_string(image, lang='por')  # 'por' para português
        return {"text": text}
    except Exception as e:
        return {"error": str(e)}

if __name__ == '__main__':
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)