// const request = require('request')

// request("https://www.useraffleit.com/shop", (error, response, body) => {
//     if (!error) {
//         // const sneakerCard = body.;
//         // const  output = sneakerCard.find(".card-title").text();
//         //  {
//         //     const name = $(sneakerCard).text();
//         //     const linkToSite = $(sneakerCard.a.link-before);
//         //     const durationOfRaffle = S(sneakerCard.card-title.text-secondary);
//         //     console.log(name, linkToSite, durationOfRaffle);
//         // }
//         console.log(body);
//     }
// })

const axios = require("axios");
const cheerio = require("cheerio");
const fs = require("fs");
const { get } = require("request");

const url ="https://www.sneaktorious.com/";


// Async function which scrapes the data
async function scrapeData() {
    try {
      // Fetch HTML of the page we want to scrape
      const { data } = await axios.get(url);
      // Load HTML we fetched in the previous line
      const $ = cheerio.load(data);
      const cardTitles = $(".card-title");
      $('h6[class="card-title"]').each((element) => {
          console.log(element.text());
      });
    //   cardTitles.each((i) => {
    //       console.log(cardTitles.get(i).text());
    //   })
    //   var list = [];

    //   $('h6[class="card-title"]')
    //             .find('> a')
    //             .each((element) => {
    //                 list.push($(element).attr("title"));
    //             })
            
                // .map((element) => {
                //     element
                // })
            

    //   console.log(list);
    //   cardTitles.each((card) => {
    //     console.log(card.text());
    //   });
    
      // Select all the list items in plainlist class
    //   const listItems = $(".plainlist ul li");
    //   // Stores data for all countries
    //   const countries = [];
    //   // Use .each method to loop through the li we selected
    //   listItems.each((idx, el) => {
    //     // Object holding data for each country/jurisdiction
    //     const country = { name: "", iso3: "" };
    //     // Select the text content of a and span elements
    //     // Store the textcontent in the above object
    //     country.name = $(el).children("a").text();
    //     country.iso3 = $(el).children("span").text();
    //     // Populate countries array with country data
    //     countries.push(country);
    //   });
    //   // Logs countries array to the console
    //   console.dir(countries);
    //   // Write countries array in countries.json file
    //   fs.writeFile("coutries.json", JSON.stringify(countries, null, 2), (err) => {
    //     if (err) {
    //       console.error(err);
    //       return;
    //     }
    //     console.log("Successfully written data to file");
    //   });
    } catch (err) {
      console.error(err);
    }
  }
  // Invoke the above function
  scrapeData();

