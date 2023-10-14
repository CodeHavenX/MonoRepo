# set base image (host OS)
FROM python:3.10-bookworm

RUN useradd --create-home --shell /bin/bash app_user
WORKDIR /home/app_user

RUN apt-get update && apt-get install -y wget tar vim

COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 5000

COPY src/ .
USER app_user
CMD ["bash", "install.sh"]
