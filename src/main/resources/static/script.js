function formatInteger(number) {
    return new Intl.NumberFormat('ru-RU').format(number);
}

function formatYear(timestamp) {
    var date = new Date(timestamp);
    return date.getFullYear();
}

function formatMonth(timestamp) {
    var date = new Date(timestamp);
    return date.toLocaleString('en', {month: 'long'}) + ' ' + date.getFullYear();
}

function formatDate(timestamp) {
    var date = new Date(timestamp);
    var parts = [
        date.getDate().toString().padStart(2, '0'),
        (date.getMonth() + 1).toString().padStart(2, '0'),
        date.getFullYear()];
    return parts.join('.')
}

function formatTime(timestamp) {
    var date = new Date(timestamp);
    var parts = [
        date.getHours().toString().padStart(2, '0'),
        date.getMinutes().toString().padStart(2, '0')];
    return parts.join(':');
}

function resolveDateFormat(dateStr) {
    var len = dateStr.length;
    if (len === 'YYYY'.length) {
        return formatYear
    } else if (len === 'YYYY-MM'.length) {
        return formatMonth;
    } else if (len === 'YYYY-MM-DD'.length) {
        return formatDate;
    } else if (len === 'YYYY-MM-DD HH:MM'.length) {
        return formatTime;
    } else {
        return function (timestamp) {
            return timestamp;
        }
    }
}