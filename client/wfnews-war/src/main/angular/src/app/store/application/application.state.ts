// import { SearchState } from "@wf1/core-ui";

// export interface PagingSearchState extends SearchState {
//     pageIndex?: number;
//     pageSize?: number;
// }
export interface PagingInfoRequest {
    query?: string;
    pageNumber: number;
    pageRowCount: number;
    sortColumn?: string;
    sortDirection?: string;
}

export interface ErrorState {
    uuid: string;
    type: ERROR_TYPE;
    status: number;
    statusText?: string;
    message?: string;
    name: string;
    validationErrors?: ValidationError[];
    responseEtag: string;
}

export enum ERROR_TYPE {
    VALIDATION,
    WARNING,
    FATAL,
    NOT_FOUND,
    FAILED_PRECONDITION
}

export interface ValidationError {
    path: string;
    message: string;
    messageTemplate: string;
    messageArguments: any[];
}

export interface ApplicationState {
    loadStates: LoadStates;
    errorStates: ErrorStates;
    formStates: FormStates;
}

export interface LoadStates {
    incidents: LoadState;
}

export interface ErrorStates {
    incidents: ErrorState[];
}

export interface FormStates{
    incidents: FormState;
}

export interface FormState {
    isUnsaved: boolean;
}

export interface LoadState {
    isLoading: boolean;
}


export function getDefaultFormState(): FormState {
    return {
        isUnsaved: false,
    };
}

export function getDefaultLoadStates(): LoadStates {
    return {
        incidents: {isLoading: false}
    };
}

export function getDefaultApplicationState(): ApplicationState {
    return {
        loadStates: getDefaultLoadStates(),
        errorStates: getDefaultErrorStates(),
        formStates: getDefaultFormStates(),
    };
}

export function getDefaultErrorStates(): ErrorStates {
    return {
        incidents: [],
    };
}

export function getDefaultFormStates(): FormStates {
    return {
        incidents: getDefaultFormState(),
    };
}
