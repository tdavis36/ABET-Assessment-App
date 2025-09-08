import { fileURLToPath, URL } from 'node:url'
import { networkInterfaces } from 'node:os'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'

function logAvailableInterfaces(): void {
  const interfaces = networkInterfaces()
  const allIPs: string[] = []
  const tailscaleIPs: string[] = []

  for (const [name, addresses] of Object.entries(interfaces)) {
    const isTailscale = name.toLowerCase().includes('tailscale') ||
      name.toLowerCase().includes('ts') ||
      name === 'tailscale0'

    // Look for Docker bridge or local network interfaces
    for (const addr of addresses || []) {
      if (addr.family === 'IPv4' && !addr.internal) {
        const ip = addr.address

        if (ip.startsWith('100.') || isTailscale) {
          tailscaleIPs.push(`${ip} (${name} - Tailscale, excluded)`)
        } else {
          allIPs.push(`${ip} (${name})`)
        }
      }
    }
  }

  // Print available IPs
  console.log('\nAvailable network interfaces:')
  console.log(`  ➜  Selected: localhost - Docker can access via host.docker.internal`)
  allIPs.forEach(ip => console.log(`     Available: ${ip}`))
  if (tailscaleIPs.length > 0) {
    tailscaleIPs.forEach(ip => console.log(`     ${ip}`))
  }
  console.log('')
}

// Log interfaces on startup
logAvailableInterfaces()

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    host: 'localhost', // Exclude Tailscale, allow Docker access via host.docker.internal
    port: 5173,
    // Proxy API requests to Spring Boot backend
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/actuator': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      }
    }
  },
  // Environment variables
  define: {
    __API_BASE_URL__: JSON.stringify(process.env.VITE_API_BASE_URL || 'http://localhost:8080')
  }
})
