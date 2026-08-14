# 🚀 Backend Python FastAPI & Modal.com (HunyuanVideo DiT)

Backend ini dirancang agar dapat di-deploy secara **100% otomatis lewat GitHub** tanpa memerlukan komputer atau laptop (cukup lewat browser HP).

---

## 📱 Cara Deploy Otomatis Lewat GitHub dari HP:

### Langkah 1: Ambil Token Modal.com (Gratis)
1. Buka [https://modal.com](https://modal.com) di browser HP Anda dan login.
2. Buka menu **Settings** -> **API Tokens** -> klik **New Token**.
3. Anda akan melihat 2 nilai:
   - `Token ID` (contoh: `ak-xxxxxxxxxx`)
   - `Token Secret` (contoh: `as-xxxxxxxxxx`)

---

### Langkah 2: Masukkan Token ke Repository GitHub Anda
1. Buka repository GitHub proyek ini di browser HP Anda.
2. Masuk ke tab **Settings** -> pilih **Secrets and variables** -> klik **Actions**.
3. Klik **New repository secret**:
   - Secret 1:
     - Name: `MODAL_TOKEN_ID`
     - Value: *(tempelkan Token ID dari Modal tadi)*
   - Secret 2:
     - Name: `MODAL_TOKEN_SECRET`
     - Value: *(tempelkan Token Secret dari Modal tadi)*

---

### Langkah 3: Jalankan Auto-Deploy (1-Klik)
1. Di repository GitHub HP Anda, masuk ke tab **Actions**.
2. Pilih workflow **"Deploy Python Backend to Modal.com"**.
3. Klik tombol **Run workflow** -> **Run workflow**.
4. GitHub Actions di cloud akan secara otomatis menginstal dependensi Python dan mendeploy backend ke server Modal.com.

---

### Langkah 4: Hubungkan ke Aplikasi Android
Setelah proses di GitHub Actions selesai (centang hijau ✅):
1. URL backend Anda akan aktif di format:
   `https://<username-modal>--hunyuanvideo-fastapi-fastapi-app.modal.run/generate`
2. Buka aplikasi **PAP AI GENERATOR** di HP Anda -> masuk ke menu **Modal Settings** (ikon tune di pojok kanan atas) -> masukkan URL tersebut -> klik **Simpan**.
