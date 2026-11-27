import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { bootstrapApplication } from '@angular/platform-browser';

interface PathologyTest {
  id: number;
  name: string;
  code: string;
  sampleType: string;
  normalRange: string;
  price: number;
}

interface LabOrder {
  id: number;
  orderNumber: string;
  patientName: string;
  phone: string;
  orderDate: string;
  testId: number;
  testName: string;
  testCode: string;
  sampleType: string;
  normalRange: string;
  price: number;
  status: 'PENDING' | 'COMPLETED';
  resultValue: string | null;
  technicianNotes: string | null;
  completedAt: string | null;
}

type Screen = 'tests' | 'orders' | 'results';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
class AppComponent implements OnInit {
  private readonly api = 'http://localhost:8080/api';
  screen: Screen = 'tests';
  tests: PathologyTest[] = [];
  orders: LabOrder[] = [];
  selectedOrder: LabOrder | null = null;
  search = '';
  selectedDate = this.today();
  error = '';
  success = '';
  busy = false;

  testDraft = { name: '', code: '', sampleType: '', normalRange: '', price: null as number | null };
  orderDraft = { patientName: '', phone: '', testId: null as number | null, orderDate: this.today() };
  resultDraft = { resultValue: '', technicianNotes: '' };

  get pendingCount(): number {
    return this.orders.filter(order => order.status === 'PENDING').length;
  }

  constructor(private readonly http: HttpClient) { }

  ngOnInit(): void {
    this.loadTests();
    this.loadOrders();
  }

  setScreen(screen: Screen): void {
    this.screen = screen;
    this.error = '';
    this.success = '';
  }

  loadTests(): void {
    this.http.get<PathologyTest[]>(`${this.api}/tests`, { params: { search: this.search } }).subscribe({
      next: tests => this.tests = tests,
      error: error => this.showError(error)
    });
  }

  createTest(): void {
    this.clearMessages();
    this.busy = true;
    this.http.post<PathologyTest>(`${this.api}/tests`, this.testDraft).subscribe({
      next: test => {
        this.tests = [test, ...this.tests];
        this.testDraft = { name: '', code: '', sampleType: '', normalRange: '', price: null };
        this.success = `${test.name} added to the test catalogue.`;
        this.busy = false;
      },
      error: error => { this.showError(error); this.busy = false; }
    });
  }

  loadOrders(): void {
    this.http.get<LabOrder[]>(`${this.api}/orders`, { params: { date: this.selectedDate } }).subscribe({
      next: orders => {
        this.orders = orders;
        if (this.selectedOrder) {
          this.selectedOrder = orders.find(order => order.id === this.selectedOrder?.id) ?? null;
        }
      },
      error: error => this.showError(error)
    });
  }

  createOrder(): void {
    this.clearMessages();
    this.busy = true;
    this.http.post<LabOrder>(`${this.api}/orders`, this.orderDraft).subscribe({
      next: order => {
        this.orderDraft = { patientName: '', phone: '', testId: null, orderDate: this.today() };
        this.selectedDate = order.orderDate;
        this.loadOrders();
        this.success = `Order ${order.orderNumber} created.`;
        this.busy = false;
      },
      error: error => { this.showError(error); this.busy = false; }
    });
  }

  selectOrder(order: LabOrder): void {
    this.clearMessages();
    if (order.status === 'COMPLETED') {
      this.http.get<LabOrder>(`${this.api}/orders/${order.id}/result`).subscribe({
        next: result => { this.selectedOrder = result; this.resultDraft = { resultValue: result.resultValue ?? '', technicianNotes: result.technicianNotes ?? '' }; },
        error: error => this.showError(error)
      });
    } else {
      this.selectedOrder = order;
      this.resultDraft = { resultValue: '', technicianNotes: '' };
    }
  }

  saveResult(): void {
    if (!this.selectedOrder) return;
    this.clearMessages();
    this.busy = true;
    this.http.put<LabOrder>(`${this.api}/orders/${this.selectedOrder.id}/result`, this.resultDraft).subscribe({
      next: result => {
        this.selectedOrder = result;
        this.orders = this.orders.map(order => order.id === result.id ? result : order);
        this.success = `Result saved for ${result.orderNumber}.`;
        this.busy = false;
      },
      error: error => { this.showError(error); this.busy = false; }
    });
  }

  private showError(error: HttpErrorResponse): void {
    this.error = error.error?.message ?? 'Could not reach the pathology service. Check that the API is running.';
    this.success = '';
  }

  private clearMessages(): void { this.error = ''; this.success = ''; }
  private today(): string {
    const date = new Date();
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
  }
}

bootstrapApplication(AppComponent, { providers: [provideHttpClient()] }).catch(error => console.error(error));
