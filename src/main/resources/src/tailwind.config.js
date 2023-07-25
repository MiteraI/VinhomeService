/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./pages/*.{html,js}", "./*{html,js}",
    "../templates/**/*.html", "../static/js/*.js"
  ],
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
        'yellow-info': '#FFD12F',
        'red-cancel': '#DC3545',
        'green-success': '#28A745'
      },
      width: {
        'inherit': 'inherit'
      },
      height: {
        'inherit': 'inherit'
      },
      fontSize: {
        'inherit': 'inherit'
      }
    },
  },
  plugins: [],
}
