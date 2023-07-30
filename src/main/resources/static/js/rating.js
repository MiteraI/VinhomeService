const allStar = document.querySelectorAll('.stars-inner')
const avgStarsPercentage = document.querySelectorAll('.avg-star-percentage')
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
  }

  console.log(avgForEachRating)

  var j = 0;
  for (let i = 0; i < allStar.length; i++) {
    let starPercentageRounded = convertToPercentage(avgRatingForEachService[j]);
    allStar[i].style.width = starPercentageRounded
    j++

  }


  return rating.data
}

getAllAvgRating()


function convertToPercentage(number) {
  let starPercentage = (parseFloat(number.toFixed(1) / starTotal)) * 100;
  let starPercentageRounded = `${parseFloat(((starPercentage / 10) * 10).toFixed(1))}%`;
  return starPercentageRounded
}