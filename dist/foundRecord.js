import { initializeApp } from "https://www.gstatic.com/firebasejs/10.11.0/firebase-app.js";
import { getFirestore, collection, getDocs } from "https://www.gstatic.com/firebasejs/10.11.0/firebase-firestore.js";

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

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

async function fetchLostAndFoundRecords() {
    const tableBody = document.querySelector("tbody");
    tableBody.innerHTML = "<tr><td colspan='5' class='text-center text-muted'>Loading...</td></tr>";

    try {
        const querySnapshot = await getDocs(collection(db, "Found_Item"));
        tableBody.innerHTML = ""; // Clear previous content

        if (querySnapshot.empty) {
            tableBody.innerHTML = "<tr><td colspan='5' class='text-center text-muted'>No records available</td></tr>";
            return;
        }

        querySnapshot.forEach((doc) => {
            const data = doc.data();
            const row = `
                <tr>
                    <td>${data.name || "Unknown"}</td>
                    <td>${data.category || "N/A"}</td>
                    <td>${data.date || "N/A"}</td>
                    <td>${data.email || "N/A"}</td>
                    <td>${data.contact || "N/A"}</td>
                </tr>
            `;
            tableBody.innerHTML += row;
        });
    } catch (error) {
        console.error("Error fetching records:", error);
        tableBody.innerHTML = "<tr><td colspan='5' class='text-center text-danger'>Error loading data</td></tr>";
    }
}

document.addEventListener("DOMContentLoaded", fetchLostAndFoundRecords);