import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';

interface AzureService {
  name: string;
  type: string;
  status: string;
  region: string;
  lastModified: string;
}

@Component({
  selector: 'app-azure',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatTableModule,
    MatChipsModule
  ],
  templateUrl: './azure.component.html',
  styleUrl: './azure.component.scss'
})
export class AzureComponent {
  displayedColumns: string[] = ['name', 'type', 'status', 'region', 'lastModified', 'actions'];
  
  azureServices: AzureService[] = [
    {
      name: 'storage-account-prod',
      type: 'Storage Account',
      status: 'Running',
      region: 'East US',
      lastModified: '2 hours ago'
    },
    {
      name: 'keyvault-secrets',
      type: 'Key Vault',
      status: 'Running',
      region: 'West US',
      lastModified: '1 day ago'
    },
    {
      name: 'queue-service-notifications',
      type: 'Service Bus',
      status: 'Running',
      region: 'Central US',
      lastModified: '3 hours ago'
    },
    {
      name: 'vm-web-server-01',
      type: 'Virtual Machine',
      status: 'Running',
      region: 'East US',
      lastModified: '30 minutes ago'
    }
  ];

  getStatusColor(status: string): 'primary' | 'accent' | 'warn' {
    switch (status.toLowerCase()) {
      case 'running': return 'primary';
      case 'stopped': return 'warn';
      case 'error': return 'warn';
      default: return 'accent';
    }
  }
}