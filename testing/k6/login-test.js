import http from 'k6/http';
import { check } from 'k6';
import { environment } from './config/setting.js';
import { workload } from './config/workload.js';
import { generateRandomFromRange } from './utils/RandomUtils.js'
import { htmlReport } from './report/libs.js';
import { textSummary } from './report/k6-summary.js';

export const options = {
    stages: workload.stress,
    thresholds: workload.thresholds
};

export default function() {
    const url = `${environment.sandbox.URL}/users/login`;
    const random_id = generateRandomFromRange(1, 1500);
    const payload = JSON.stringify({
        "username": `user_${random_id}`,
        "password": "123456"
    });

    const params = {
        headers: {
            'Content-Type': 'application/json'
        }
    };

    const res = http.post(url, payload, params);
    check(res, {
        'is status 200': (r) => r.status === 200,
    });
}

export function handleSummary(data) {
  return {
    './output/login.html': htmlReport(data),
    stdout: textSummary(data, { indent: ' ', enableColors: true }),
  }
}