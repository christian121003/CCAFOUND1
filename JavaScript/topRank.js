import { initializeApp } from "https://www.gstatic.com/firebasejs/9.0.0/firebase-app.js";
import { getFirestore, collection, query, orderBy, limit, getDocs } from "https://www.gstatic.com/firebasejs/9.0.0/firebase-firestore.js";

const firebaseConfig = {
  apiKey: "AIzaSyB30G6YH-oj2sfNTNdSe3MYI9J4Ag_7Q0I",
  authDomain: "ccafound-74e1a.firebaseapp.com",
  databaseURL: "https://ccafound-74e1a-default-rtdb.firebaseio.com",
  projectId: "ccafound-74e1a",
  storageBucket: "ccafound-74e1a.appspot.com",
  messagingSenderId: "795614038924",
  appId: "1:795614038924:web:da93f83c00ae3eb59f24a9",
  measurementId: "G-BRX6CMHGE9"
};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);

// Function to get initials
function getInitials(name) {
  const nameParts = name.split(" ");
  const initials = nameParts.length > 1 
    ? nameParts[0].charAt(0) + nameParts[1].charAt(0) 
    : nameParts[0].charAt(0);
  return initials.toUpperCase();
}

async function loadTopFoundReporters() {
  const container = document.getElementById("topReporters");
  const loader = document.getElementById("loader");
  loader.style.display = "block";
  container.innerHTML = "";

  const q = query(collection(db, "users"), orderBy("foundCount", "desc"), limit(10));
  const querySnapshot = await getDocs(q);

  let rank = 1;
  querySnapshot.forEach((doc) => {
    const data = doc.data();
    const name = data.name || "Unknown";
    const count = data.foundCount || 0;
    const imageUrl = data.profileImageUrl || "N/A"; // Use "N/A" if no image URL is provided

    const card = `
      <div class="col-md-10 mb-3">
        <div class="d-flex align-items-center p-3 bg-light rounded shadow-sm">
          <div class="badge bg-primary rounded-circle me-3" style="width: 40px; height: 40px; display: flex; justify-content: center; align-items: center;">${rank}</div>
          ${imageUrl === "N/A" ? `
            <div class="rounded-circle me-3" style="width: 60px; height: 60px; background-color: #ccc; display: flex; justify-content: center; align-items: center;">
              <span class="text-white" style="font-size: 20px;">${getInitials(name)}</span>
            </div>
          ` : `
            <img src="${imageUrl}" onerror="this.onerror=null; this.src='https://via.placeholder.com/60'" alt="Student" class="rounded-circle me-3" style="width: 60px; height: 60px; object-fit: cover;" />
          `}
          <div>
            <h5 class="mb-0 fw-bold">${name}</h5>
            <small class="text-muted">Reported: ${count}</small>
          </div>
        </div>
      </div>
    `;
    container.insertAdjacentHTML("beforeend", card);
    rank++;
  });

  loader.style.display = "none";
}

window.addEventListener("DOMContentLoaded", () => {
  console.log("Page loaded");
  loadTopFoundReporters();
});