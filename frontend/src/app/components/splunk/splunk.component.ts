import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';

interface SplunkLog {
  timestamp: string;
  level: string;
  source: string;
  message: string;
  count: number;
}

@Component({
  selector: 'app-splunk',
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
  templateUrl: './splunk.component.html',
  styleUrl: './splunk.component.scss'
})
export class SplunkComponent {
  displayedColumns: string[] = ['timestamp', 'level', 'source', 'message', 'count'];
  
  splunkLogs: SplunkLog[] = [
    {
      timestamp: '2025-10-05 10:30:15',
      level: 'INFO',
      source: 'web-server-01',
      message: 'User login successful for user: john.doe@company.com',
      count: 1
    },
    {
      timestamp: '2025-10-05 10:29:42',
      level: 'ERROR',
      source: 'api-gateway',
      message: 'Failed to connect to database: Connection timeout after 30s',
      count: 3
    },
    {
      timestamp: '2025-10-05 10:28:33',
      level: 'WARN',
      source: 'load-balancer',
      message: 'High CPU usage detected on server-02: 85%',
      count: 1
    },
    {
      timestamp: '2025-10-05 10:27:18',
      level: 'INFO',
      source: 'cache-service',
      message: 'Cache miss for key: user_preferences_12345',
      count: 1
    },
    {
      timestamp: '2025-10-05 10:26:55',
      level: 'DEBUG',
      source: 'auth-service',
      message: 'Token validation completed successfully',
      count: 5
    }
  ];

  getLevelColor(level: string): 'primary' | 'accent' | 'warn' {
    switch (level.toUpperCase()) {
      case 'ERROR': return 'warn';
      case 'WARN': return 'warn';
      case 'INFO': return 'primary';
      case 'DEBUG': return 'accent';
      default: return 'primary';
    }
  }
}