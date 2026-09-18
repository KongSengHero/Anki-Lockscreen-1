import tailwindcss from '@tailwindcss/vite' 
import react from '@vitejs/plugin-react' 
import { defineConfig } from 'vite' 
import { VitePWA } from 'vite-plugin-pwa' 

export default defineConfig({
  plugins: [
    react(), 
    tailwindcss(), 
    VitePWA({
      registerType: 'autoUpdate', 
      includeAssets: ['favicon.ico', 'apple-touch-icon.png', 'sql-wasm.wasm'], 
      manifest: {
        name: 'Blossom Japanese & Anki', 
        short_name: 'Blossom', 
        description: 'Japanese Learning & Spaced Repetition Flashcard Reader', 
        theme_color: '#0D111A', 
        background_color: '#080C14', 
        display: 'standalone', 
        orientation: 'portrait', 
        icons: [
          {
            src: 'pwa-192x192.png', 
            sizes: '192x192', 
            type: 'image/png' 
          }, 
          {
            src: 'pwa-512x512.png', 
            sizes: '512x512', 
            type: 'image/png' 
          }, 
          {
            src: 'pwa-512x512.png', 
            sizes: '512x512', 
            type: 'image/png', 
            purpose: 'any maskable' 
          } 
        ] 
      } 
    }) 
  ] 
}) 
