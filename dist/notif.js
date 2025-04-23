function toggleNotificationBar() {
    const bar = document.getElementById('notificationBar');
    bar.classList.toggle('active');
}

function removeNotification(id) {
    const notification = document.getElementById(id);
    if (notification) {
        notification.remove();
    }
}