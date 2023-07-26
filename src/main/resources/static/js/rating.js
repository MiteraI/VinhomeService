const allStar = document.querySelectorAll('.stars-inner')
const avgStars = document.querySelectorAll('.avg-stars')
const avgStarsPercentage = document.querySelectorAll('.avg-star-percentage')
const avgRatingContainer = document.querySelectorAll('.avgRatingContainer')
let avgRatingForEachService = []
let avgForEachRating = {}

const starsContainer = document.querySelectorAll('.stars-container')
const queryString = window.location.href.split(/(\\|\/)/g).pop()

const percentageText = document.querySelectorAll('.percentage-text')
const starTotal = 5;

let reverseArr = []

const getAllAvgRating = async () => {
  let rating = await axios.get(`/api/services/avg-rating/${queryString}`)

  console.log(rating.data)
  console.log(rating.data.length)

  for (let i = 0; i < rating.data.length; i++) {
    if (i % 2 == 0) {
      for (let j = 0; j < rating.data[i].length; j++) {
        console.log(rating.data[i][j])
        avgRatingForEachService.push(rating.data[i][j])
      }
    }
    else {
      for (let key in rating.data[i]) {
        avgForEachRating[`${key}`] = rating.data[i][key].reverse()

      }
    }
  }

  console.log(avgForEachRating)

  var j = 0;
  for (let i = 0; i < allStar.length; i++) {
    if (i % 2 == 0) {
      let starPercentageRounded = convertToPercentage(avgRatingForEachService[j]);
      allStar[i].style.width = starPercentageRounded
      allStar[i + 1].style.width = starPercentageRounded
      avgStars[j].innerHTML = `${parseFloat(avgRatingForEachService[j]).toFixed(1)} out of 5 stars`
      j++
    }

  }


  return rating.data
}

getAllAvgRating()

for (let i = 0; i < starsContainer.length; i++) {
  starsContainer[i].addEventListener('mouseover', (e) => {
    e.preventDefault()
    avgRatingContainer[i].classList.toggle('hidden')
    var j = 0
    var z = 0
    var percentage = 0
    for (let key in avgForEachRating) {
      for (let k = j; k < avgForEachRating[key].length + j; k++) {
        percentage = parseFloat(avgForEachRating[key][z]).toFixed(2) * 100
        avgStarsPercentage[k].style.width = `${percentage}%`;
        percentageText[k].innerHTML = `${parseFloat(percentage).toFixed(0)}%`
        z++;
      }
      j = z
      z = 0
    }
  })
  starsContainer[i].addEventListener('mouseout', () => {
    avgRatingContainer[i].classList.toggle('hidden')
  })
}

function convertToPercentage(number) {
  let starPercentage = (parseFloat(number.toFixed(1) / starTotal)) * 100;
  let starPercentageRounded = `${parseFloat(((starPercentage / 10) * 10).toFixed(1))}%`;
  return starPercentageRounded
}