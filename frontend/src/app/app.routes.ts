import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { 
    path: 'dashboard', 
    loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
  { 
    path: 'azure', 
    loadComponent: () => import('./components/azure/azure.component').then(m => m.AzureComponent)
  },
  { 
    path: 'gcp', 
    loadComponent: () => import('./components/gcp/gcp.component').then(m => m.GcpComponent)
  },
  { 
    path: 'splunk', 
    loadComponent: () => import('./components/splunk/splunk.component').then(m => m.SplunkComponent)
  },
  { 
    path: 'monitoring', 
    loadComponent: () => import('./components/monitoring/monitoring.component').then(m => m.MonitoringComponent)
  },
  { path: '**', redirectTo: '/dashboard' }
];