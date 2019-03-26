const routes = [
    'products',
    'products/station',
    'products/station/with-mini',
    'products/mini',
    'products/mini/wifi',
    'products/mini/wifi/with-flex',
    'products/mini/lte',
    'products/mini/lte/with-flex',
    'integrations',
    'integrations/partners',
    'careers'
];

const output = {
    products: {
        station: {
            'with-mini': {}
        },
        mini: {
            wifi: {
                'with-flex': {}
            },
            lte: {
                'with-flex': {}
            }
        }
    },
    integrations: {
        partners: {}
    },
    careers: {}
}



let result = {};

for(let i = 0; i < routes.length; i++) {

    let currentRoute = routes[i];
    let currentRouteParts = currentRoute.split('/'); // [products,  station, with-mini]

    let topLevelObj = null;
    let nestedLevel = null;
    let thisLevel = null;

    for(let j = 0; j < currentRouteParts.length; j++) {
        let currentRouteName = currentRouteParts[j];

        if (!topLevelObj) {
            topLevelObj = {}
        }
    }

    Object.assign(result, topLevelObj);

}

let findParentLevel = (currentRouteParts) => {
    let lastFound = result;
    for(let i = 0; i < currentRouteParts.length; i++) {
        let currentRoutePart = currentRouteParts[i];
        if (result[currentRoutePart]){
            lastFound = result[currentRoutePart];
        } else {
            break;
        }
    }

    return lastFound;
};

console.log(result);
