import pandas as pd
from datetime import datetime
import json


def read_file(file_path):
    try:
        df = pd.read_excel(file_path)
        required_columns = ['Kod_programu', 'nazwa', 'data_od', 'forma', 'stopien']

        missing_columns = [col for col in required_columns if col.strip() not in df.columns.str.strip()]
        if missing_columns:
            raise ValueError(f"Missing columns: {', '.join(missing_columns)}")  
        
        df.drop(columns=[col for col in df.columns if col not in required_columns], inplace=True)
        df.rename(columns = {'data_od' :'cykl_dyd'}, inplace = True)

        df = df.fillna('')
        
        df['Kod_programu'] = df['Kod_programu'].str.upper()
        df['cykl_dyd'] = df['cykl_dyd'].astype(str)

        df['Year'] = df['cykl_dyd'].str.extract(r'(\d{4})').astype(int)
        df['NextYear'] = df['Year'] + 1
        df['FormattedDate'] = df['Year'].astype(str) + '/' + df['NextYear'].astype(str).str[-2:]
        df['cykl_dyd'] = df['FormattedDate']
        df.drop(['Year', 'NextYear', 'FormattedDate'], axis=1, inplace=True)

        df['nazwa'] = df['nazwa'].str.split(',').str[0].str.strip().str.lower()
        print(df['nazwa'].unique())

        df[['Kod_wydzialu', 'Kod_kierunku', 'Kod_specjalnosci', 'Not_necessary']] = df['Kod_programu'].str.split('-', expand=True)
        df['Kod_kierunku'] = df['Kod_kierunku'].str[:3]
        df['Kod_specjalnosci'] = df['Kod_specjalnosci'].str[:3]
        df.drop('Not_necessary', axis=1, inplace=True)

        print(df)

    except pd.errors.ParserError as e:
        raise ValueError("Error parsing the file. Please check the file format and structure.") from e


def dataframes_to_json(df_valid, invalid_program_codes, invalid_names, invalid_edu_cycles, invalid_study_forms, invalid_study_stages, invalid_field_codes, invalid_speciality_codes, invalid_faculty_codes, invalid_faculty, invalid):
    pass

def main():
    file_path = "src/test/resources/kody_prog.xlsx" 
    read_file(file_path)


if __name__ == '__main__':
    main()
