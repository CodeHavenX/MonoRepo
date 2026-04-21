# FlyerBoard Deployment Guide

This directory contains configuration files for deploying FlyerBoard on a VPS using
Nginx as a reverse proxy and systemd for process management.

## Prerequisites

- A Linux server (Ubuntu/Debian recommended)
- Java 17+ installed
- Nginx installed
- A [Supabase](https://supabase.com) project with migrations applied (see `back-end/supabase/README.md`)

---

## 1. Build the Application

### Backend (fat JAR)

```bash
./gradlew :flyerboard:back-end:buildFatJar
```

The output JAR is located at:
```
flyerboard/back-end/build/libs/flyerboard-back-end.jar
```

### Frontend (WASM)

```bash
./gradlew :flyerboard:front-end:app-wasm:wasmJsBrowserDistribution
```

The output static files are located at:
```
flyerboard/front-end/app-wasm/build/dist/wasmJs/productionExecutable/
```

---

## 2. Apply Database Migrations

Apply the migrations in `back-end/supabase/migrations/` to your Supabase project in order:

```bash
supabase db push
```

Or apply each file manually in the Supabase SQL editor in chronological order.

See `back-end/supabase/README.md` for full migration and storage bucket setup instructions.

---

## 3. Configure Environment Variables

Copy `env.example` to `/etc/flyerboard/env` and fill in your values:

```bash
sudo mkdir -p /etc/flyerboard
sudo cp deploy/env.example /etc/flyerboard/env
sudo nano /etc/flyerboard/env
sudo chmod 600 /etc/flyerboard/env
```

Required variables (note: all `FLYERBOARD_` prefixed variables are auto-transformed by the
application's `EnvironmentConfiguration` using domain prefix `"FLYERBOARD"`):

| Variable                           | Description                                         |
|------------------------------------|-----------------------------------------------------|
| `FLYERBOARD_SUPABASE_URL`          | Your Supabase project URL                           |
| `FLYERBOARD_SUPABASE_KEY`          | Supabase **service role** key (not the anon key)    |
| `PORT`                             | Port for the Ktor backend (e.g. `8080`)             |
| `FLYERBOARD_FLYER_MAX_FILE_SIZE_BYTES` | Max upload size in bytes (e.g. `10485760` for 10MB) |

---

## 4. Deploy the Backend

Create a dedicated system user and deploy the JAR:

```bash
sudo useradd --system --no-create-home --shell /usr/sbin/nologin flyerboard
sudo mkdir -p /opt/flyerboard
sudo cp flyerboard/back-end/build/libs/flyerboard-back-end.jar /opt/flyerboard/
sudo chown -R flyerboard:flyerboard /opt/flyerboard
```

Install and start the systemd service:

```bash
sudo cp deploy/flyerboard.service /etc/systemd/system/
sudo systemctl daemon-reload
sudo systemctl enable flyerboard
sudo systemctl start flyerboard
sudo systemctl status flyerboard
```

---

## 5. Deploy the Frontend

Copy the WASM build output to the Nginx web root:

```bash
sudo mkdir -p /var/www/flyerboard
sudo cp -r flyerboard/front-end/app-wasm/build/dist/wasmJs/productionExecutable/. /var/www/flyerboard/
```

---

## 6. Configure Nginx

Update `nginx.conf` with your actual domain name, then install it:

```bash
sudo cp deploy/nginx.conf /etc/nginx/sites-available/flyerboard
sudo ln -s /etc/nginx/sites-available/flyerboard /etc/nginx/sites-enabled/flyerboard
sudo nginx -t
sudo systemctl reload nginx
```

> **TLS/SSL:** Obtain a certificate (e.g., with Let's Encrypt / Certbot) and update the
> `ssl_certificate` and `ssl_certificate_key` paths in `nginx.conf` accordingly.

---

## 7. Bootstrap the First Admin User

After the first user signs up via the app, promote them to admin via the Supabase SQL editor:

```sql
UPDATE public.user_profiles
SET role = 'admin'
WHERE id = '<user-uuid>';
```

Replace `<user-uuid>` with the user's UUID from `auth.users`.

---

## Updating the Application

To deploy a new version:

1. Build the new JAR and/or frontend (steps 1 above).
2. Copy the new JAR to `/opt/flyerboard/flyerboard-back-end.jar`.
3. Copy updated frontend files to `/var/www/flyerboard/`.
4. Restart the backend: `sudo systemctl restart flyerboard`.
