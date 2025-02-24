import { Component } from '@angular/core';
import { Router, NavigationStart, Event as RouterEvent, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'frontend';

  constructor(private router: Router) {
    // Verificar soporte para View Transitions
    this.setupViewTransitions();
  }

  private setupViewTransitions(): void {
    // Comprobar si el navegador soporta View Transitions API
    const supportsViewTransitions = 'startViewTransition' in document;
    
    if (supportsViewTransitions) {
      console.log('View Transitions API está soportada en este navegador');
      
      // No aplicamos transiciones automáticas aquí porque lo hacemos a nivel de componente

      this.router.events.pipe(
        filter((e): e is NavigationEnd => e instanceof NavigationEnd)
      ).subscribe(() => {
        // Navegación completada - aquí podríamos hacer algo si es necesario
      });
    } else {
      console.log('View Transitions API no está soportada en este navegador');
    }
  }
}