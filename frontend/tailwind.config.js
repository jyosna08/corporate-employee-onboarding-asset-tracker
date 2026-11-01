/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  safelist: [{ pattern: /^(bg|text)-(indigo|amber|blue|green)-(50|600)$/ }],
  theme: { extend: { colors: { ink: '#202735', muted: '#7c8492', line: '#e9ebf0', brand: '#4969e4' }, fontFamily: { sans: ['DM Sans', 'sans-serif'], display: ['Manrope', 'sans-serif'] }, boxShadow: { card: '0 1px 3px rgba(20,30,55,.06)' } } },
  plugins: [],
};
