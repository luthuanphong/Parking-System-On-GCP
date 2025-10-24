import loginTest from './login-test.js';
import getReservation from './get-reservations-test.js';
import getParkingSpot from './get-parking-spots-test.js';
import depositTest from './deposit-test.js';
import { workload } from './config/workload.js';
import { generateRandomFromRange } from './utils/RandomUtils.js'
import { htmlReport } from './report/libs.js';
import { textSummary } from './report/k6-summary.js';


export const options = {
    stages: workload.superStress,
    thresholds: workload.thresholds
};

export default function() {
    const randomNum = generateRandomFromRange(1,4);
    executeApi(randomNum);
}

function executeApi(num) {
    switch(num) {
        case 2:
            return getReservation();
        case 3:
            return getParkingSpot();
        case 4:
            return depositTest();
        case 5:
            return loginTest();
        default:
            return loginTest(); 
    }
}

export function handleSummary(data) {
  return {
    './output/login.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  }
}