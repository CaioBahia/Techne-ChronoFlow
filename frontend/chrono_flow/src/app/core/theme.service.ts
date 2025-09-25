import { Injectable, signal, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  isDark = signal<boolean>(false);

  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    if (isPlatformBrowser(this.platformId)) {
      const storedPreference = localStorage.getItem('theme');
      const systemPreference = window.matchMedia('(prefers-color-scheme: dark)');

      if (storedPreference) {
        this.isDark.set(storedPreference === 'dark');
      } else {
        this.isDark.set(systemPreference.matches);
      }

      this.updateBodyClass();

      systemPreference.addEventListener('change', e => {
        if (!localStorage.getItem('theme')) {
          this.isDark.set(e.matches);
          this.updateBodyClass();
        }
      });
    }
  }

  toggleTheme(): void {
    if (isPlatformBrowser(this.platformId)) {
      this.isDark.set(!this.isDark());
      localStorage.setItem('theme', this.isDark() ? 'dark' : 'light');
      this.updateBodyClass();
    }
  }

  private updateBodyClass(): void {
    if (isPlatformBrowser(this.platformId)) {
      if (this.isDark()) {
        document.body.classList.add('dark-mode');
      } else {
        document.body.classList.remove('dark-mode');
      }
    }
  }
}
