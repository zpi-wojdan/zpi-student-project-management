import { TFunction } from "i18next";
import { toast } from "react-toastify";

export const handleDeletionError = (error: any, t: TFunction<"translation", undefined>, deletedFrom: string) => {

    if (error.response.status === 405 && error.response.data.message) {
        const errorMessage = error.response.data.message;

        if (errorMessage.includes('foreign key constraint')) {
            const tableNameRegex = /table "(?<table>[a-zA-Z0-9_]+)"/mg;
            const tableNames = [];

            let match;
            while ((match = tableNameRegex.exec(errorMessage)) !== null) {
                const tableName = match.groups?.table;
                tableNames.push(tableName);
            }

            if (tableNames.length === 3) {
                const deletedTable = tableNames[0];
                const referencedTable = tableNames[1];

                const translatedReferencedTable = t(`tables.${referencedTable}`, { defaultValue: referencedTable });

                const translatedMessage = t(`${deletedTable}.deleteErrorWithReference`, {
                    referencedTable: translatedReferencedTable
                });

                toast.error(translatedMessage);
                return;
            }
        } else if (errorMessage.includes('null value in column')) {
            const relationRegex = /relation "(?<table>[a-zA-Z0-9_]+)"/mg;
            const tableNames = [];
            let match;

            while ((match = relationRegex.exec(errorMessage)) !== null) {
                const tableName = match.groups?.table;
                tableNames.push(tableName);
            }

            if (tableNames.length === 1) {
                const deletedTable = deletedFrom;
                const referencedTable = tableNames[0];

                const translatedReferencedTable = t(`tables.${referencedTable}`, { defaultValue: referencedTable });

                const translatedMessage = t(`${deletedTable}.deleteErrorWithReference`, {
                    referencedTable: translatedReferencedTable
                });

                toast.error(translatedMessage);
                return;
            }
        }
    }

    toast.error(t(`${deletedFrom}.deleteError`));
};
