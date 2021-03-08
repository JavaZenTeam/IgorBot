function formatInteger(number) {
    return new Intl.NumberFormat('ru-RU').format(number);
}

function formatYear(timestamp) {
    let date = new Date(timestamp);
    return date.getFullYear();
}

function formatMonth(timestamp) {
    let date = new Date(timestamp);
    return date.toLocaleString('en', {month: 'long'}) + ' ' + date.getFullYear();
}

function formatDate(timestamp) {
    let date = new Date(timestamp);
    let parts = [
        date.getDate().toString().padStart(2, '0'),
        (date.getMonth() + 1).toString().padStart(2, '0'),
        date.getFullYear()];
    return parts.join('.')
}

function formatTime(timestamp) {
    let date = new Date(timestamp);
    let parts = [
        date.getHours().toString().padStart(2, '0'),
        date.getMinutes().toString().padStart(2, '0')];
    return parts.join(':');
}

function formatDateTime(timestamp) {
    return formatDate(timestamp) + '\n' + formatTime(timestamp);
}