/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./pages/*.{html,js}", "./*{html,js}"],
  theme: {
    screens: {
      xm: '250px',
      sm: '640px',
      md: '768px',
      lg: '1024px',
      xl: '1280px',
      xxl: '1536px'
    },
    extend: {
      colors: {
        'blue-bootstrap': '#007BFF',
        'strong-gray': '#535151',
        'yello-info': '#FFD12F'
      }
    },
  },
  plugins: [],
}

