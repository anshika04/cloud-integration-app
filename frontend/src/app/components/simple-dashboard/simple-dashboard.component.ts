import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-simple-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="padding: 20px;">
      <h1>Cloud Integration Dashboard</h1>
      <p>Welcome to the Cloud Integration Platform!</p>
      <div style="margin-top: 20px;">
        <h2>Available Services:</h2>
        <ul>
          <li>Azure Services</li>
          <li>GCP Services</li>
          <li>Splunk Logging</li>
          <li>System Monitoring</li>
        </ul>
      </div>
      <div style="margin-top: 20px;">
        <h2>System Status</h2>
        <p>✅ Backend API: Connected</p>
        <p>✅ Database: Connected</p>
        <p>✅ Frontend: Running</p>
      </div>
    </div>
  `,
  styles: []
})
export class SimpleDashboardComponent {
  constructor() {}
}
