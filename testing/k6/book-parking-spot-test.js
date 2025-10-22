import http from 'k6/http';
import { environment } from './config/setting.js';
import { workload } from './config/workload.js';
import { generateRandomFromRange } from './utils/RandomUtils.js'
import { htmlReport } from './report/libs.js';
import { textSummary } from './report/k6-summary.js';
import { tokens } from './mock/mock.js';

export const options = {
    stages: workload.stress,
    thresholds: workload.thresholds
};


export default function() {
    const url = `${environment.sandbox.URL}/parking/reservations`
    const random_id = generateRandomFromRange(1, 1500);
    const random_spot_id = generateRandomFromRange(1, 80);
    const username =  `user_${random_id}`;
    const token = tokens[username];

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        responseCallback: http.expectedStatuses(200, 403, 404, 409, 429)
    };

    const payload = JSON.stringify({
        "spotId": random_spot_id
    });

    const res = http.post(url, payload, params);
}


export function handleSummary(data) {
  return {
    './output/booking.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  }
}