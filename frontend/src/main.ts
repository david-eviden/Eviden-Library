import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';

// Importar el compilador antes de iniciar el bootstrap
import '@angular/compiler';

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));