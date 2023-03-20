from itertools import count
from tokenize import String
from bs4 import BeautifulSoup
import requests

url = 'https://www.sneaktorious.com/'
data = requests.get(url)
html = BeautifulSoup(data.text, 'html.parser')
cards = html.select("div.card")
titles = html.select("h6.card-title")
releaseMonths = html.select("span.h4")
getTitle = lambda title: title.select("h6.card-title > a")[0].text


cardData = []

for card in cards:
    try:
        title = card.select("h6.card-title > a")[0].text
        releaseMonth = card.select("span.h4")[0].text
        releaseDay = card.select("span.h5")[0].text
        imageLink = card.find("img")['src']
        linkToSite = "sneaktorious.com"+card.find("a")['href']
        print(title, releaseMonth, releaseDay, imageLink, linkToSite)
        cardData.append({"title": title, "releaseMonth": releaseMonth, "releaseDay": releaseDay, "imageLink": imageLink, "Site Link" : linkToSite})
    except:
        continue

print(cardData)


import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore
import datetime
from datetime import datetime
import pytz

tz = pytz.timezone('America/Los_Angeles')
time = datetime.now(tz).strftime('%Y-%m-%d %H:%M:%S')

cred = credentials.Certificate("useraffleit-sourcing-firebase-adminsdk-zf6w6-b8b43c0eb6.json")
firebase_admin.initialize_app(cred)
db = firestore.client()

def save(collection_id, document_id, data):
    count = 0
    for i in range(len(data)):
            count = count + 1
            strCount = str(count)
            db.collection(collection_id).document(strCount).set(data[i])


save(
    collection_id = "Sneaktorious Data",
    document_id = f"{time}",
    data = cardData
)




# for title in titles:
#     print(title)

# for releaseMonth in releaseMonths:
#     print(rel)

# for title in titles:  
#     a = title.select("a")
#     print(a[0].text)
