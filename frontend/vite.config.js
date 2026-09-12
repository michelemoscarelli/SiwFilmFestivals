import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
// Build con output fisso (niente hash) dentro src/main/resources/static/film-app, montato dalla
// pagina Thymeleaf templates/film/elenco.html tramite <div id="root">. Stesso schema di
// camere-app/classifica-app nei progetti di riferimento (SIWHotel-definitivo, FootballSiw).
export default defineConfig(function (_a) {
    var command = _a.command;
    return ({
        plugins: [react()],
        base: command === 'build' ? '/film-app/' : '/',
        build: {
            outDir: '../src/main/resources/static/film-app',
            emptyOutDir: true,
            rollupOptions: {
                output: {
                    entryFileNames: 'assets/film.js',
                    chunkFileNames: 'assets/film-[name].js',
                    assetFileNames: 'assets/film.[ext]',
                },
            },
        },
    });
});
