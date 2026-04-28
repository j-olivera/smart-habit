export interface HandleError {
    code: 'EMAIL_TAKEN' | 'NOT_FOUND' |
    'UNAUTHORIZED' | 'INSUFFICIENT_DATA' |
    'INVALID_CREDENTIALS' | 'SERVER_DOWN' | 'UNKNOW_ERROR';
    details?: {
        email?: string;
        password?: string;
    }
}