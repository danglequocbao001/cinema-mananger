const dates = (startDate, num) =>
  Array.from({ length: num }, (_, i) =>
    new Date(startDate.getTime() + i * 60000 * 60 * 24)
      .toISOString()
      .slice(0, 10)
  );

//Lấy thông tin từ cookies mà database trả đến
cals = getCookie("calendar-dates")
  .split("-")
  .map((i) => new Date(parseInt(i)));
days = {};
for (let i = 0; i < cals.length; i++) {
  key = `${cals[i].getDate()}-${cals[i].getMonth()}-${cals[i].getFullYear()}`;
  if (days[key] == undefined) {
    days[key] = [];
  }
  days[key] = days[key].concat(cals[i]);
}

//Xuất ra các select theo ngày
function initDay() {
  for (let day of Object.keys(days)) {
    let selectDay = document.getElementById("select-day-father");
    var optionDay = document.createElement("option");
    optionDay.value = day;
    optionDay.innerHTML = day;
    selectDay.appendChild(optionDay);
  }
}

//Xuất ra các select từng khung giờ chiếu trong ngày
function initTime(day, days) {
  let selectTime = document.getElementById("select-time-father");
  selectTime.innerHTML = "";
  for (let time of days[day]) {
    var optionTime = document.createElement("option");
    optionTime.value = time;
    optionTime.setAttribute("value", time);
    optionTime.innerHTML = time.toString().substring(16, 21);
    selectTime.appendChild(optionTime);
  }
}

function onChangeDay() {
  var day = document.getElementById("select-day-father").value;
  console.log(day);
  initTime(day, days);
}

function onChangeTime() {
  var time = document.getElementById("select-time-father").value;
  console.log(time);
}

initDay();
initTime(Object.keys(days)[0], days);

function getValue() {
  setCookie('ticket-time', `${Date.parse(document.getElementById("select-time-father").value)}`, 10);
  window.location.href = '/film-ticketing-second-step';
}