// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyB30G6YH-oj2sfNTNdSe3MYI9J4Ag_7Q0I",
  authDomain: "ccafound-74e1a.firebaseapp.com",
  databaseURL: "https://ccafound-74e1a-default-rtdb.firebaseio.com",
  projectId: "ccafound-74e1a",
  storageBucket: "ccafound-74e1a.firebasestorage.app",
  messagingSenderId: "795614038924",
  appId: "1:795614038924:web:da93f83c00ae3eb59f24a9",
  measurementId: "G-BRX6CMHGE9"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
