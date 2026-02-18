import type { Config } from "tailwindcss";

const config: Config = {
  darkMode: ["class"],
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        app: {
          bg: "#f7f7f7",
          darkBg: "#141414",
          surface: "#ffffff",
          darkSurface: "#1e1e1e",
          line: "#e5e7eb",
          darkLine: "#313131",
          text: "#1f2937",
          darkText: "#f3f4f6",
          muted: "#6b7280",
          accent: "#db4c3f",
          accentHover: "#c63f33"
        }
      },
      fontFamily: {
        sans: ["ui-sans-serif", "system-ui", "Segoe UI", "sans-serif"]
      }
    }
  },
  plugins: []
};

export default config;
