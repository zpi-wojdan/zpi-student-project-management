import pandas as pd
from datetime import datetime
import json
from tabulate import tabulate


def read_file_prog(file_path) -> pd.DataFrame:
    try:
        df = pd.read_excel(file_path)
        required_columns = ['Kod_programu', 'nazwa', 'data_od', 'forma', 'stopien']

        missing_columns = [col for col in required_columns if col.strip() not in df.columns.str.strip()]
        if missing_columns:
            raise ValueError(f"Missing columns: {', '.join(missing_columns)}")  
        
        df.drop(columns=[col for col in df.columns if col not in required_columns], inplace=True)
        df.rename(columns = {'data_od' :'Cykl_dyd'}, inplace = True)
        df.rename(columns = {'nazwa' :'Nazwa_kierunku'}, inplace = True)
        df.rename(columns = {'forma' :'Forma'}, inplace = True)
        df.rename(columns = {'stopien' :'Stopien'}, inplace = True)

        df = df.fillna('')
        
        df['Kod_programu'] = df['Kod_programu'].str.upper()

        df['Cykl_dyd'] = pd.to_datetime(df['Cykl_dyd'])
        threshold_date = pd.to_datetime('2021-01-01')
        df = df[df['Cykl_dyd'] >= threshold_date]
        df['Cykl_dyd'] = df['Cykl_dyd'].astype(str)

        df['Year'] = df['Cykl_dyd'].str.extract(r'(\d{4})').astype(int)
        df['NextYear'] = df['Year'] + 1
        df['FormattedDate'] = df['Year'].astype(str) + '/' + df['NextYear'].astype(str).str[-2:]
        df['Cykl_dyd'] = df['FormattedDate']
        df.drop(['Year', 'NextYear', 'FormattedDate'], axis=1, inplace=True)

        df['Nazwa_kierunku'] = df['Nazwa_kierunku'].str.split(',').str[0].str.strip().str.capitalize()
        print(df['Nazwa_kierunku'].unique())

        df[['Kod_wydzialu', 'Kod_kierunku', 'Kod_specjalizacji', 'Not_necessary']] = df['Kod_programu'].str.split('-', expand=True)
        df['Kod_kierunku'] = df['Kod_kierunku'].str[:3]
        df['Kod_specjalizacji'] = df['Kod_specjalizacji'].str[:3]
        df.drop('Not_necessary', axis=1, inplace=True)

        df = df[df['Kod_wydzialu'] == 'W04']

        df = df.reindex(columns=['Kod_programu', 'Nazwa_kierunku', \
                                 'Kod_kierunku', 'Kod_specjalizacji',\
                                'Cykl_dyd', 'Forma', 'Stopien', 'Kod_wydzialu'])
        return df

    except pd.errors.ParserError as e:
        raise ValueError("Error parsing the file. Please check the file format and structure.") from e


def read_file_kier(file_path: str, df: pd.DataFrame):
    try:
        df_kier = pd.read_excel(file_path)
        required_columns = ['Kod', 'nazwa', 'kod kierunku nadrzednego', 'typ kodu']

        missing_columns = [col for col in required_columns if col.strip() not in df_kier.columns.str.strip()]
        if missing_columns:
            raise ValueError(f"Missing columns: {', '.join(missing_columns)}")  
        df_kier.drop(columns=[col for col in df_kier.columns if col not in required_columns], inplace=True)
        df_kier.rename(columns = {'typ kodu' :'typ_kodu'}, inplace = True)
        df_kier.rename(columns = {'kod kierunku nadrzednego' :'kod_kierunku_nadrzednego'}, inplace = True)
        
        df_kier = df_kier.fillna('')

        df_kier['Kod'] = df_kier['Kod'].str.upper()
        df_kier['nazwa'] = df_kier['nazwa'].str.capitalize()
        df_kier['kod_kierunku_nadrzednego'] = df_kier['kod_kierunku_nadrzednego'].str.upper()
        
        df['Nazwa_specjalizacji'] = ''

        for index, row in df_kier.iterrows():
            code = row['Kod'].split('-')
            # fields = ['CBE', 'INA', 'IST', 'INF', 'SZT', 'TAI', 'TEL', 'TIN', 'ISA']
            fields = ['INA', 'IST', 'INF']
            if (len(code) == 2):
                # print(code[1])
                if code[0] in fields:
                    field = [f for f in fields if f == code[0]]
                    print(str(field) + ' - ' + str(code[1]))
                    condition = df['Kod_kierunku'] == code[1]
                    value = row['nazwa']
                    df.loc[condition, 'Nazwa_specjalizacji'] = value

        # print(tabulate(df, headers='keys', tablefmt='psql'))


    except pd.errors.ParserError as e:
        raise ValueError("Error parsing the file. Please check the file format and structure.") from e



def dataframes_to_json(df_valid, invalid_program_codes, invalid_field_names,\
                        invalid_field_codes, invalid_specialty_names,\
                        invalid_specialty_codes, invalid_edu_cycles,\
                        invalid_study_forms, invalid_study_stages,\
                        invalid_faculty_codes, invalid_faculty_names) -> str:
    try:
        df_valid_json = df_valid.to_json(orient='records')
        invalid_program_codes_json = invalid_program_codes.to_json(orient='records')
        invalid_field_names_json = invalid_field_names.to_json(orient='records')
        invalid_field_codes_json = invalid_field_codes.to_json(orient='records')
        invalid_specialty_names_json = invalid_specialty_names.to_json(orient='records')
        invalid_specialty_codes_json = invalid_specialty_codes.to_json(orient='records')
        invalid_edu_cycles_json = invalid_edu_cycles.to_json(orient='records')
        invalid_study_forms_json = invalid_study_forms.to_json(orient='records')
        invalid_study_stages_json = invalid_study_stages.to_json(orient='records')
        invalid_faculty_codes_json = invalid_faculty_codes.to_json(orient='records')
        invalid_faculty_names_json = invalid_faculty_names.to_json(orient='records')

        full_json = {
            'valid_data': json.loads(df_valid_json),
            'invalid_program_codes': json.loads(invalid_program_codes_json),
            'invalid_field_names': json.loads(invalid_field_names_json),
            'invalid_field_codes': json.loads(invalid_field_codes_json),
            'invalid_specialty_names': json.loads(invalid_specialty_names_json),
            'invalid_specialty_codes': json.loads(invalid_specialty_codes_json),
            'invalid_edu_cycles': json.loads(invalid_edu_cycles_json),
            'invalid_study_forms': json.loads(invalid_study_forms_json),
            'invalid_study_stages': json.loads(invalid_study_stages_json),
            'invalid_faculty_codes': json.loads(invalid_faculty_codes_json),
            'invalid_faculty_names': json.loads(invalid_faculty_names_json)
        }
        output = json.dumps(full_json, indent=3, allow_nan=True)
        return output
    except json.JSONDecodeError as jserr:
        print(f"Error occured while decoding JSON: {str(jserr)}")


def main():
    file_path_prog = 'zpi-backend/src/test/resources/kody_prog.xlsx'
    file_path_kier = 'zpi-backend/src/test/resources/kody_kier.xlsx'
    df = read_file_prog(file_path_prog)
    read_file_kier(file_path_kier, df)


if __name__ == '__main__':
    main()
