import { ErrorHandler } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { alert } from 'devextreme/ui/dialog';

export class ExceptionHandler implements ErrorHandler {

    type: string = 'error';
    time: number = 10000;

    public handleError(error: Error | HttpErrorResponse) {
        console.warn(error);
        if (error instanceof HttpErrorResponse) {
            let statusCode: number = error.status;
            if (statusCode == 504) this.show(error.error);
            if (!navigator.onLine) this.show('Sem conexão com a internet');
            else if (error.error.errors) this.show(this.formatErrors(error.error.errors));
            else this.show(error.error.message);
        } else this.show(error);
    }

    show(error) {
        alert(error, 'Aviso!');
    }

    formatErrors(errors) {
        let formatText = '<ul>';
        errors.forEach(error => formatText += `<li>${error.field} ${error.defaultMessage}</li>`);
        formatText += '</ul>'
        return formatText;
    }

}