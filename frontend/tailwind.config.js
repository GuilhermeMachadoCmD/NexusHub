/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        './src/pages/**/*.{js,ts,jsx,tsx,mdx}',
        './src/components/**/*.{js,ts,jsx,tsx,mdx}',
        './src/app/**/*.{js,ts,jsx,tsx,mdx}',
    ],
    theme: {
        extend: {
            colors: {
                brand: {
                    50: '#f0f1ff',
                    100: '#e0e2ff',
                    200: '#c7caff',
                    300: '#a5a8fc',
                    400: '#8b85f8',
                    500: '#7c6ff1',
                    600: '#6d54e5',
                    700: '#5d43ca',
                    800: '#4c38a3',
                    900: '#413481',
                },
            },
            fontFamily: {
                sans: ['Inter', 'system-ui', 'sans-serif'],
            },
        },
    },
    plugins: [],
}
