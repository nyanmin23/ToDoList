# Frontend (Backend-Synced)

## Stack
- React + Vite + TypeScript (strict mode)
- React Router
- Tailwind CSS
- Axios with interceptors
- Zod request/response validation

## Run
```bash
npm install
cp .env.example .env
npm run dev
```

## Environment
- `VITE_API_BASE_URL=http://localhost:8080`

## Build
```bash
npm run build
```

## Notes
- Session-cookie auth (`withCredentials: true`)
- CSRF header (`X-XSRF-TOKEN`) is sent automatically for non-GET requests when token cookie exists
- Global 401 and 500 handling via Axios interceptors
