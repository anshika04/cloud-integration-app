import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CloudFile {
  name: string;
  size: number;
  lastModified: Date;
}

export interface CloudMessage {
  message: string;
  timestamp: Date;
}

export interface HealthStatus {
  azure: string;
  gcp: string;
  splunk: string;
}

@Injectable({
  providedIn: 'root'
})
export class CloudService {
  private apiUrl = '/api';

  constructor(private http: HttpClient) {}

  // Azure Services
  uploadToAzure(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/cloud/azure/upload`, formData);
  }

  downloadFromAzure(filename: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/cloud/azure/download/${filename}`, {
      responseType: 'blob'
    });
  }

  sendToAzureQueue(message: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/cloud/azure/queue`, { message });
  }

  // GCP Services
  uploadToGcp(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.apiUrl}/cloud/gcp/upload`, formData);
  }

  downloadFromGcp(filename: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/cloud/gcp/download/${filename}`, {
      responseType: 'blob'
    });
  }

  publishToGcpPubSub(message: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/cloud/gcp/pubsub`, { message });
  }

  listGcpFiles(): Observable<any> {
    return this.http.get(`${this.apiUrl}/cloud/gcp/files`);
  }

  deleteGcpFile(filename: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/cloud/gcp/files/${filename}`);
  }

  // Splunk Services
  logToSplunk(event: string, source: string, sourcetype: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/cloud/splunk/log`, {
      event,
      source,
      sourcetype
    });
  }

  // Health Check
  getHealthStatus(): Observable<HealthStatus> {
    return this.http.get<HealthStatus>(`${this.apiUrl}/cloud/health`);
  }
}
