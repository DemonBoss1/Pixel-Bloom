import json
import time
import requests
import os
import base64  # Импортируем base64 для декодирования
from io import BytesIO
from PIL import Image

class FusionBrainAPI:

    def __init__(self, url, api_key, secret_key):
        self.URL = url
        self.AUTH_HEADERS = {
            'X-Key': f'Key {api_key}',
            'X-Secret': f'Secret {secret_key}',
        }

    def get_pipeline(self):
        response = requests.get(self.URL + 'key/api/v1/pipelines', headers=self.AUTH_HEADERS)
        data = response.json()
        return data[0]['id']

    def generate(self, prompt, pipeline, images=1, width=1024, height=1024):
        params = {
            "type": "GENERATE",
            "numImages": images,
            "width": width,
            "height": height,
            "generateParams": {
                "query": f'{prompt}'
            }
        }

        data = {
            'pipeline_id': (None, pipeline),
            'params': (None, json.dumps(params), 'application/json')
        }
        response = requests.post(self.URL + 'key/api/v1/pipeline/run', headers=self.AUTH_HEADERS, files=data)
        data = response.json()
        return data['uuid']

    def check_generation(self, request_id, attempts=10, delay=10):
        while attempts > 0:
            response = requests.get(self.URL + 'key/api/v1/pipeline/status/' + request_id, headers=self.AUTH_HEADERS)
            data = response.json()
            if data['status'] == 'DONE':
                return data['result']['files']  # Возвращаем данные файлов (Base64)

            attempts -= 1
            time.sleep(delay)

    def save_image_from_base64(self, base64_string, filename):
        """Декодирует Base64 строку и сохраняет изображение."""
        try:
            image_data = base64.b64decode(base64_string)  # Декодируем Base64
            image = Image.open(BytesIO(image_data))  # Открываем изображение с помощью Pillow
            image.save(filename, "PNG")  # Сохраняем изображение в формате PNG
            print(f"Изображение сохранено как: {filename}")

        except Exception as e:
            print(f"Ошибка при сохранении изображения: {e}")


if __name__ == '__main__':
    api = FusionBrainAPI('https://api-key.fusionbrain.ai/', '2645A3AA716D52F54884C2F2AE036CB0', 'BE85DA3215D2DFA09BDAC3FCFDACFEB9')
    pipeline_id = api.get_pipeline()
    uuid = api.generate("Sun in sky", pipeline_id)
    files = api.check_generation(uuid)
    print(files)

    if files:
        output_folder = "generated_images"
        if not os.path.exists(output_folder):
            os.makedirs(output_folder)

        if isinstance(files, list):  # Если возвращается список изображений
            for i, file_info in enumerate(files):
                # Проверяем, что file_info - это строка (предполагаем, что base64 непосредственно в списке)
                if isinstance(file_info, str):
                    try:
                        image_data = base64.b64decode(file_info)
                        filename = os.path.join(output_folder, f"image_{i+1}.png")
                        image = Image.open(BytesIO(image_data))
                        image.save(filename, "PNG")
                        print(f"Изображение {i+1} сохранено как {filename}")
                    except Exception as e:
                        print(f"Ошибка при обработке изображения {i+1}: {e}")
                else:
                    print(f"Элемент списка files[{i}] не является строкой.  Содержимое: {file_info}")

        elif isinstance(files, dict) and 'base64' in files:  # Если возвращается одно изображение
            image_base64 = files['base64']
            filename = os.path.join(output_folder, "image_1.png")
            api.save_image_from_base64(image_base64, filename)
        else:
            print("Не удалось определить структуру данных 'files'. Проверьте вывод API.")