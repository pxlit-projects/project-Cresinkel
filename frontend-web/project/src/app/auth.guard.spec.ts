import { TestBed } from '@angular/core/testing';
import { AuthGuard } from './auth.guard';  // Import the guard
import { AuthService } from './auth.service';  // Import AuthService (as AuthGuard depends on it)
import { RouterTestingModule } from '@angular/router/testing';
import {Router} from "@angular/router";  // Import RouterTestingModule to mock Router

describe('AuthGuard', () => {
  let guard: AuthGuard;
  let authServiceSpy: jasmine.SpyObj<AuthService>;  // Spy for AuthService
  let routerSpy: jasmine.SpyObj<Router>;  // Spy for Router

  beforeEach(() => {
    // Create spy objects for AuthService and Router
    authServiceSpy = jasmine.createSpyObj('AuthService', ['currentUserValue']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],  // Mock Router with RouterTestingModule
      providers: [
        AuthGuard,  // Provide the AuthGuard service
        { provide: AuthService, useValue: authServiceSpy },  // Provide AuthService spy
        { provide: Router, useValue: routerSpy }  // Provide Router spy
      ]
    });
    guard = TestBed.inject(AuthGuard);  // Get the instance of the guard
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
