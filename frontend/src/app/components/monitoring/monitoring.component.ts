import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';

interface SystemMetric {
  service: string;
  cpu: number;
  memory: number;
  disk: number;
  status: string;
  uptime: string;
}

@Component({
  selector: 'app-monitoring',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatChipsModule,
    MatProgressBarModule
  ],
  templateUrl: './monitoring.component.html',
  styleUrl: './monitoring.component.scss'
})
export class MonitoringComponent {
  displayedColumns: string[] = ['service', 'cpu', 'memory', 'disk', 'status', 'uptime'];
  
  systemMetrics: SystemMetric[] = [
    {
      service: 'Web Server-01',
      cpu: 35,
      memory: 58,
      disk: 45,
      status: 'Healthy',
      uptime: '15 days, 8 hours'
    },
    {
      service: 'Database Server',
      cpu: 68,
      memory: 82,
      disk: 85,
      status: 'Warning',
      uptime: '23 days, 12 hours'
    },
    {
      service: 'API Gateway',
      cpu: 42,
      memory: 45,
      disk: 32,
      status: 'Healthy',
      uptime: '8 days, 3 hours'
    },
    {
      service: 'Cache Server',
      cpu: 25,
      memory: 78,
      disk: 15,
      status: 'Healthy',
      uptime: '45 days, 2 hours'
    },
    {
      service: 'Load Balancer',
      cpu: 15,
      memory: 35,
      disk: 28,
      status: 'Healthy',
      uptime: '67 days, 15 hours'
    }
  ];

  getStatusColor(status: string): 'primary' | 'accent' | 'warn' {
    switch (status.toLowerCase()) {
      case 'healthy': return 'primary';
      case 'warning': return 'warn';
      case 'critical': return 'warn';
      default: return 'accent';
    }
  }
}