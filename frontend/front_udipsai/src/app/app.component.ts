import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent implements OnInit{
  title = 'frontend-udipsai';
  products: any[] = [];

  ngOnInit() {
    this.products = [
      { code: 'P001', name: 'Product 1', category: 'Category 1', quantity: 10 },
      { code: 'P002', name: 'Product 2', category: 'Category 2', quantity: 20 },
      { code: 'P003', name: 'Product 3', category: 'Category 3', quantity: 30 },
      { code: 'P004', name: 'Product 4', category: 'Category 4', quantity: 40 },
      { code: 'P005', name: 'Product 5', category: 'Category 5', quantity: 50 },
    ];
  }
}
