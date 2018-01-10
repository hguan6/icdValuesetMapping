import pandas as pd

if __name__ == '__main__':
    valueset = pd.read_excel("../resources/dataset/ValueSetWithSensitiveCategory.xlsx",
                             sheet_name='Code with sensitive category',
                             index_col=None,                             
                             usecols=[0,2,6]
                             )
    icd_valueset = valueset.loc[valueset['Code System'] == 'ICD10CM']

    icd_valueset.to_csv("../resources/dataset/generated/valueset.csv",
                        columns=['Code','Sensitive category'],
                        header=False,
                        index=False
                        )

