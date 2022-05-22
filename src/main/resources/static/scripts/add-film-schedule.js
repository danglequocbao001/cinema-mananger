const arrTimes = splitFilmDurationInDay(120 * 60); //film duration by seconds = minutes * 60
let arrDays = getDatesInRange("22-05-2022", "31-05-2022"); //generate all day in range, get from film info
let arrDayTimeChoosed = [];
setCookie("_arr_day_time", JSON.stringify(arrDayTimeChoosed), 1);

function splitFilmDurationInDay(filmDuration) {
  var closeTimeInSecons = 23 * 60 * 60;
  var arrFilmDurations = [];
  var temparrFilmDurations = [];
  var pad = function (num, size) {
    return ("0" + num).slice(size * -1);
  };
  var conv = function (t) {
    var time = parseFloat(t).toFixed(3),
      hours = Math.floor(time / 60 / 60),
      minutes = Math.floor(time / 60) % 60;

    return pad(hours, 2) + ":" + pad(minutes, 2);
  };

  for (
    var t = 7 * 60 * 60;
    t <= closeTimeInSecons - (closeTimeInSecons % filmDuration) + filmDuration;
    t += filmDuration
  ) {
    arrFilmDurations.push(conv(Math.min(t, closeTimeInSecons)));
  }

  for (let arrFilmDuration of arrFilmDurations) {
    if (
      arrFilmDurations.indexOf(arrFilmDuration) ==
      arrFilmDurations.length - 1
    ) {
      break;
    } else {
      temparrFilmDurations.push(
        arrFilmDuration +
          " - " +
          arrFilmDurations[arrFilmDurations.indexOf(arrFilmDuration) + 1]
      );
    }
  }

  return temparrFilmDurations.slice();
}

function getDatesInRange(startDate, endDate) {
  startDate =
    startDate.substring(6) +
    "-" +
    startDate.substring(3, 5) +
    "-" +
    startDate.substring(0, 2);

  endDate =
    endDate.substring(6) +
    "-" +
    endDate.substring(3, 5) +
    "-" +
    endDate.substring(0, 2);

  const d1 = new Date(startDate);
  const d2 = new Date(endDate);

  const date = new Date(d1.getTime());
  const dates = [];
  date.setDate(date.getDate() + 1);
  while (date <= d2) {
    const tmpDate = new Date(date).toISOString().slice(0, 10);
    dates.push(
      tmpDate.substring(8) +
        "-" +
        tmpDate.substring(5, 7) +
        "-" +
        tmpDate.substring(0, 4)
    );
    date.setDate(date.getDate() + 1);
  }
  return dates;
}

function setCookie(name, value, days) {
  var expires = "";
  if (days) {
    var date = new Date();
    date.setTime(date.getTime() + days * 24 * 60 * 60 * 1000);
    expires = "; expires=" + date.toUTCString();
  }
  document.cookie = name + "=" + (value || "") + expires + "; path=/";
}

function initDayTimeChoose() {
  for (let arrDay of arrDays) {
    const bottomContainer = document.getElementById("bottomDayTime");
    const day = arrDay;
    const chooseTimeCover = document.createElement("div");
    const dayTitle = document.createElement("h2");
    const fatherDayTime = document.createElement("div");

    chooseTimeCover.className = "choose-time-cover";
    dayTitle.innerText = day;
    fatherDayTime.style.marginTop = "20px";
    fatherDayTime.className = "row";

    for (let arrTime of arrTimes) {
      const childDayTime = document.createElement("h5");
      childDayTime.id = arrDay.replace(" ", "") + arrTime.replace(/\s/g, "");
      childDayTime.className = "choose-time-unactive";
      childDayTime.innerText = arrTime;

      childDayTime.addEventListener("click", function () {
        if (childDayTime.className.includes("choose-time-unactive")) {
          addTimeChoosed(childDayTime);
        } else if (childDayTime.className.includes("choose-time-active")) {
          deleteTimeChoosed(childDayTime);
        }
      });

      fatherDayTime.appendChild(childDayTime);
    }
    chooseTimeCover.appendChild(dayTitle);
    chooseTimeCover.appendChild(fatherDayTime);
    bottomContainer.appendChild(chooseTimeCover);
  }
}

function addTimeChoosed(childDayTime) {
  childDayTime.className = "choose-time-active";
  arrDayTimeChoosed.push({
    day: childDayTime.id.substring(0, 10),
    time: childDayTime.id.substring(10).replace(/\s/g, ""),
  });
  setCookie("_arr_day_time", JSON.stringify(arrDayTimeChoosed), 1);
}

function deleteTimeChoosed(childDayTime) {
  childDayTime.className = "choose-time-unactive";
  var index = arrDayTimeChoosed.findIndex((object) => {
    return object.day + object.time == childDayTime.id;
  });
  if (index > -1) {
    arrDayTimeChoosed.splice(index, 1);
    setCookie("_arr_day_time", JSON.stringify(arrDayTimeChoosed), 1);
  }
}

initDayTimeChoose();
