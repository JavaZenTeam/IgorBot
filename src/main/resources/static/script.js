function formatDate(date) {
    var parts = [
        date.getDate().toString().padStart(2, '0'),
        (date.getMonth()+1).toString().padStart(2, '0'),
        date.getFullYear()];
    return parts.join('.')
}