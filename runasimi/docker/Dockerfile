FROM python:3.10.13-bullseye
RUN /bin/sh -c useradd --create-home --shell /bin/bash app_user
USER app_user
WORKDIR /home/app_user
RUN /bin/sh -c apt-get update && apt-get install -y wget tar vim
COPY src/ .
COPY requirements.txt ./
RUN /bin/sh -c pip install --no-cache-dir -r requirements.txt
EXPOSE 5000
CMD ["bash" "install.sh"]
