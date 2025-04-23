import { initializeApp } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-app.js";
import { getFirestore, collection, addDoc } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-firestore.js";
import { getStorage, ref, uploadBytes, getDownloadURL } from "https://www.gstatic.com/firebasejs/10.8.0/firebase-storage.js";

const firebaseConfig = {
  apiKey: "AIzaSyB30G6YH-oj2sfNTNdSe3MYI9J4Ag_7Q0I",
  authDomain: "ccafound-74e1a.firebaseapp.com",
  databaseURL: "https://ccafound-74e1a-default-rtdb.firebaseio.com",
  projectId: "ccafound-74e1a",
  storageBucket: "ccafound-74e1a.appspot.com",
  messagingSenderId: "795614038924",
  appId: "1:795614038924:web:da93f83c00ae3eb59f24a9"
};

const app = initializeApp(firebaseConfig);
const db = getFirestore(app);
const storage = getStorage(app);

document.querySelector('form').addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const contact = document.getElementById('contact').value.trim();
    const category = document.querySelector('input[placeholder="Category"]').value.trim();
    const date = document.querySelector('input[type="date"]').value;
    const description = document.querySelector('textarea').value.trim();
    const file = document.getElementById('fileUpload').files[0];

    if (!file) {
        alert("Please upload an image.");
        return;
    }

    try {
        const fileRef = ref(storage, 'Found_Items/' + Date.now() + '_' + file.name);
        await uploadBytes(fileRef, file);
        const imageUrl = await getDownloadURL(fileRef);

        await addDoc(collection(db, 'foundItems'), {
            name,
            email,
            contact,
            category,
            dateFound: date,
            description,
            imageUrl
        });

        alert("Item uploaded successfully!");
        document.querySelector('form').reset();
    } catch (error) {
        console.error("Upload failed:", error);
        alert("Error uploading item: " + error.message);
    }
});
