import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditDraftComponent } from './edit-draft.component';
import {HttpClientTestingModule} from "@angular/common/http/testing";
import {RouterTestingModule} from "@angular/router/testing";
import {FormsModule} from "@angular/forms";

describe('EditDraftComponent', () => {
  let component: EditDraftComponent;
  let fixture: ComponentFixture<EditDraftComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        EditDraftComponent,
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule,
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditDraftComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
