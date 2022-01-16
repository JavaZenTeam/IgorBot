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

function getAvailableIntervalOptions() {
    let hoursInUnit = {
        'HOUR': 1,
        'DAY': 24,
        'MONTH': 24 * 30,
        'YEAR': 24 * 365
    };
    let intervalOptions = [
        [1, 'HOUR'],
        [2, 'HOUR'],
        [3, 'HOUR'],
        [6, 'HOUR'],
        [1, 'DAY'],
        [2, 'DAY'],
        [3, 'DAY'],
        [7, 'DAY'],
        [14, 'DAY'],
        [1, 'MONTH'],
        [2, 'MONTH'],
        [3, 'MONTH'],
        [6, 'MONTH'],
        [1, 'YEAR']
    ].map(item => {
        return {
            quantity: item[0],
            unit: item[1],
            hoursDuration: item[0] * hoursInUnit[item[1]]
        }
    });
    let periodDuration = getPeriodHoursDuration();
    return intervalOptions.filter(function (item) {
        return periodDuration >= 3 * item.hoursDuration &&
            periodDuration <= 50 * item.hoursDuration;
    });
}


function getPeriodHoursDuration() {
    return 24 + (getTime(getToDate()) - getTime(getFromDate())) / (1000 * 60 * 60)
}

function getTime(date) {
    let a = date.split('.');
    return new Date(a[2], a[1] - 1, a[0]).getTime();
}

function resolveDateFormat(unit) {
    switch (unit) {
        case 'HOUR':
            if (getPeriodHoursDuration() === 24) {
                return formatTime;
            } else {
                return formatDateTime;
            }
        case 'DAY':
            return formatDate;
        case 'MONTH':
            return formatMonth;
        case 'YEAR':
            return formatYear;
        default:
            return formatDateTime;
    }
}

class ChatActivityChart {
    constructor(containerElementId, chartElementId) {
        this.containerElementId = containerElementId;
        this.chartElementId = chartElementId;
        this.intervalOptions;
        this.chartControl;
        this.chartSvg;
        this.chartData;
        this.ids;
        this.selectedIds;
        this.labels;

        this.getMode = function () {
            return $(`#${this.containerElementId} select[name=activity-chart-mode]`).val();
        }

        this.getAttribute = function() {
            return $(`#${this.containerElementId} select[name=activity-chart-attribute]`).val();
        }

        this.getInterval = function() {
            let intervalElement = $(`#${this.containerElementId} select[name=activity-chart-interval]`);
            return this.intervalOptions[intervalElement.val()];
        }

        this.buildChart = function(response) {
            this.chartData = response['data'];
            this.ids = response['ids'];
            this.labels = response['labels'];
            let dateFormat = resolveDateFormat(this.getInterval().unit);
            this.chartControl = Morris.Area({
                element: this.chartElementId,
                data: this.chartData,
                xkey: 0,
                ykeys: this.filteredYKeys(),
                labels: this.filteredLabels(),
                xLabels: this.getInterval().unit.toLowerCase(),
                xLabelFormat: dateFormat,
                dateFormat: dateFormat,
                yLabelFormat: formatInteger,
                hideHover: true,
                behaveLikeLine: true,
                fillOpacity: 0.0,
                resize: true,
                smooth: false
            });
            this.chartSvg = $(`#${this.chartElementId}>svg`);
            if (dateFormat === formatDateTime) {
                this.chartSvg.height(chartSvg.height() + 10);
            }
        }

        this.filterSelectedIds = function() {
            let selectedIds = this.selectedIds;
            let ids = this.ids;
            return function(item, index) {
                return selectedIds.indexOf("" + ids[index]) !== -1;
            }
        }

        this.filteredYKeys = function() {
            return Array.from(Array(this.ids.length).keys())
                .filter(this.filterSelectedIds())
                .map(i => i + 1);
        }

        this.filteredLabels = function() {
            return this.labels.filter(this.filterSelectedIds());
        }

        this.refreshChartView = function() {
            let dateFormat = resolveDateFormat(this.getInterval().unit);
            if (dateFormat === formatDateTime) {
                //to fit date time label into canvas. part 1
                this.chartSvg.height(this.chartSvg.height() - 10);
            }
            this.chartControl.options.xLabelFormat = dateFormat;
            this.chartControl.options.dateFormat = dateFormat;
            this.chartControl.options.ykeys = this.filteredYKeys();
            this.chartControl.options.labels = this.filteredLabels();
            this.chartControl.options.xLabels = this.getInterval().unit.toLowerCase();
            switch (this.getMode()) {
                case 'SUM':
                    this.sumMode();
                    break;
                case 'RATIO':
                    this.ratioMode();
                    break;
                default:
                    this.defaultMode();
            }
            if (dateFormat === formatDateTime) {
                //to fit date time label into canvas. part 2
                this.chartSvg.height(this.chartSvg.height() + 10);
            }
        }

        this.defaultMode = function() {
            this.chartControl.cumulative = false;
            this.chartControl.options.behaveLikeLine = true;
            this.chartControl.options.fillOpacity = 0.0;
            this.chartControl.options.postUnits = '';
            this.chartControl.options.ymax = 'auto';
            this.chartControl.setData(this.chartData);
        }

        this.sumMode = function() {
            this.chartControl.cumulative = true;
            this.chartControl.options.behaveLikeLine = false;
            this.chartControl.options.fillOpacity = 1.0;
            this.chartControl.options.postUnits = '';
            this.chartControl.options.ymax = 'auto';
            this.chartControl.setData(this.chartData);
        }

        this.ratioMode = function() {
            this.chartControl.cumulative = true;
            this.chartControl.options.behaveLikeLine = false;
            this.chartControl.options.fillOpacity = 1.0;
            this.chartControl.options.postUnits = '%';
            this.chartControl.options.ymax = 100.0;
            let filterSelectedIds = this.filterSelectedIds();

            let data = this.chartData.map(function (item) {
                let result = [item[0]];
                let filteredData = item.slice(1)
                                .filter(filterSelectedIds);
                let sum = filteredData.reduce((a, b) => a + b, 0);
                for (let i = 1; i < item.length; i++) {
                    let ratio = sum === 0
                        ? 1.0 / filteredData.length * 100.0
                        : item[i] / sum * 100.0;
                    result.push(ratio.toFixed(1));
                }
                return result;
            });
            this.chartControl.setData(data);
        }

        this.refreshChartData = function() {
            let path = window.location.pathname + "/activity-chart";
            let param = $.param({
                from: getFromDate(),
                to: getToDate(),
                attribute: this.getAttribute(),
                intervalQuantity: this.getInterval().quantity,
                intervalUnit: this.getInterval().unit,
                chatType: getChatType(),
            });
            let that = this;
            $.get(path + "?" + param)
                .done(function (response) {
                    that.chartData = response['data'];
                    that.refreshChartView();
                });
        }

        this.renderIntervalOptions = function() {
            let selectElement = $(`#${this.containerElementId} select[name=activity-chart-interval]`);
            this.intervalOptions.forEach(function (item, i) {
                let text = item.quantity + ' ' + item.unit.toLowerCase() + (item.quantity > 1 ? 's' : '');
                selectElement.append('<option value="' + i + '">' + text + '</option>');
            })
            selectElement.val('1');
        }

        this.init = function() {
            let that = this;
            this.selectedIds = $('.row-select[checked]').map((i, el) => el.id.split('/')[1]).toArray();

            $('.row-select')
                .change((e) => {
                    let objectId = e.target.id.split('/')[1];
                    if (e.target.checked) {
                        that.selectedIds.push(objectId);
                    } else {
                        that.selectedIds.splice(that.selectedIds.indexOf(objectId), 1);
                    }
                    that.refreshChartView();
                });

            this.intervalOptions = getAvailableIntervalOptions();
            this.renderIntervalOptions();
            let path = window.location.pathname + "/activity-chart";
            let param = $.param({
                from: getFromDate(),
                to: getToDate(),
                intervalQuantity: this.getInterval().quantity,
                intervalUnit: this.getInterval().unit,
                chatType: getChatType(),
            });
            $.get(path + "?" + param)
                .done(function (response) {
                    if (response['data'].length > 0 && response['data'][0].length > 1) {
                        that.buildChart(response);
                    } else {
                        $(`#${that.containerElementId}`).parent().replaceWith(
                            '<div class="alert-info p-3 mb-3">' +
                            'No data found for the selected period' +
                            '</div>');
                    }
                });
            $(`#${this.containerElementId} select[name=activity-chart-mode]`)
                .change(() => that.refreshChartView());
            $(`#${this.containerElementId} select[name=activity-chart-attribute]`)
                .change(() => that.refreshChartData());
            $(`#${this.containerElementId} select[name=activity-chart-interval]`)
                .change(() => that.refreshChartData());
        }

        this.init();
    }
}

class AdminActivityChart {
    constructor(containerElementId, chartElementId, url) {
        this.containerElementId = containerElementId;
        this.chartElementId = chartElementId;
        this.url = url;
        this.chartControl = null;
        this.chartSvg = null;
        this.chartData = null;
        this.intervalOptions = getAvailableIntervalOptions();

        this.getInterval = function () {
            return this.intervalOptions[$(`#${this.containerElementId} select[name=activity-chart-interval]`).val()];
        }

        this.buildChart = function (response) {
            this.chartData = response['data'];
            let dateFormat = resolveDateFormat(this.getInterval().unit);
            this.chartControl = Morris.Area({
                element: this.chartElementId,
                data: this.chartData,
                xkey: 0,
                ykeys: response['ids'],
                labels: response['labels'],
                xLabels: this.getInterval().unit.toLowerCase(),
                xLabelFormat: dateFormat,
                dateFormat: dateFormat,
                yLabelFormat: formatInteger,
                hideHover: true,
                behaveLikeLine: true,
                fillOpacity: 0.0,
                resize: true,
                smooth: false
            });
            this.chartSvg = $(`#${this.chartElementId} > svg`);
            if (dateFormat === formatDateTime) {
                this.chartSvg.height(chartSvg.height() + 10);
            }
        }

        this.refreshChartView = function () {
            let dateFormat = resolveDateFormat(this.getInterval().unit);
            if (dateFormat === formatDateTime) {
                //to fit date time label into canvas. part 1
                this.chartSvg.height(this.chartSvg.height() - 10);
            }
            this.chartControl.options.xLabelFormat = dateFormat;
            this.chartControl.options.dateFormat = dateFormat;
            this.chartControl.options.xLabels = this.getInterval().unit.toLowerCase();
            this.defaultMode();
            if (dateFormat === formatDateTime) {
                //to fit date time label into canvas. part 2
                this.chartSvg.height(this.chartSvg.height() + 10);
            }
        }

        this.defaultMode = function () {
            this.chartControl.cumulative = false;
            this.chartControl.options.behaveLikeLine = true;
            this.chartControl.options.fillOpacity = 0.0;
            this.chartControl.options.postUnits = '';
            this.chartControl.options.ymax = 'auto';
            this.chartControl.setData(this.chartData);
        }

        this.refreshChartData = function () {
            let path = window.location.pathname + "/" + this.url;
            let param = $.param({
                from: getFromDate(),
                to: getToDate(),
                intervalQuantity: this.getInterval().quantity,
                intervalUnit: this.getInterval().unit
            });
            let that = this;
            $.get(path + "?" + param)
                .done(function (response) {
                    that.chartData = response['data'];
                    that.refreshChartView();
                });
        }

        this.renderIntervalOptions = function () {
            let selectElement = $(`#${this.containerElementId} select[name=activity-chart-interval]`);
            this.intervalOptions.forEach(function (item, i) {
                let text = item.quantity + ' ' + item.unit.toLowerCase() + (item.quantity > 1 ? 's' : '');
                selectElement.append('<option value="' + i + '">' + text + '</option>');
            })
            selectElement.val('1');
        }

        this.init = function () {


            this.renderIntervalOptions();
            let interval = this.getInterval();
            let path = window.location.pathname + "/" + this.url;
            let param = $.param({
                from: getFromDate(),
                to: getToDate(),
                intervalQuantity: interval.quantity,
                intervalUnit: interval.unit
            });
            let that = this;
            $.get(path + "?" + param)
                .done(function (response) {
                    if (response['data'].length > 0 && response['data'][0].length > 1) {
                        that.buildChart(response);
                    } else {
                        $('#' + that.containerElementId).parent().replaceWith(
                            '<div class="alert-info p-3 mb-3">' +
                            'No data found for the selected period' +
                            '</div>');
                    }
                });

            $(`#${this.containerElementId} select[name=activity-chart-interval]`)
                .change(() => that.refreshChartData());
        }

        this.init();
    }
}