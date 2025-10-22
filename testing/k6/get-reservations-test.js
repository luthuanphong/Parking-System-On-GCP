import http from 'k6/http';
import { check } from 'k6';
import { environment } from './config/setting.js';
import { workload } from './config/workload.js';
import { tokens } from './mock/mock.js';
import { generateRandomFromRange } from './utils/RandomUtils.js'
import { htmlReport } from './report/libs.js';
import { textSummary } from './report/k6-summary.js';

export const options = {
    stages: workload.stress,
    thresholds: workload.thresholds
};

export default function() {
    const url = `${environment.sandbox.URL}/parking/reservations`
    const random_id = generateRandomFromRange(1, 1500);
    const username =  `user_${random_id}`;
    const token = tokens[username];

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    const res = http.get(url, params);
    check(res, {
        'is status 200': (r) => r.status === 200,
    });
}

export function handleSummary(data) {
  return {
    './output/get-reservations.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  }
}