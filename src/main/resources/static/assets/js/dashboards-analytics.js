/**
 * Dashboard Analytics
 */

'use strict';

(function () {
    let cardColor, headingColor, axisColor, shadeColor, borderColor;

    cardColor = config.colors.white;
    headingColor = config.colors.headingColor;
    axisColor = config.colors.axisColor;
    borderColor = config.colors.borderColor;

    // Total Revenue Report Chart - Bar Chart
    // --------------------------------------------------------------------
    const totalRevenueChartEl = document.querySelector('#totalRevenueChart');

    if (totalRevenueChartEl) {
        const urlParams = new URLSearchParams(window.location.search);
        const cardNumber = urlParams.get('cardNumber');

        let apiUrl = '/api/clients/statistics';
        if (cardNumber) {
            apiUrl += `?cardNumber=${encodeURIComponent(cardNumber)}`;
        }

        fetch(apiUrl)
            .then(response => response.json())
            .then(data => {
                const diagram = data.incomeExpensesDiagram || {};
                const incomes = diagram.incomes || {};
                const expenses = diagram.expenses || {};

                // Extract month names
                const incomeMonths = Object.keys(incomes);
                const expenseMonths = Object.keys(expenses);

                // Combine and deduplicate months from both
                const allMonths = Array.from(new Set([...incomeMonths, ...expenseMonths])).sort((a, b) => {
                    const monthOrder = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
                    return monthOrder.indexOf(a) - monthOrder.indexOf(b);
                });

                // Fallback defaults if API returned no data
                const defaultMonths = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'];
                const defaultIncomeData = [18, 7, 15, 29, 18, 12, 9];
                const defaultExpenseData = [-13, -18, -9, -14, -5, -17, -15];

                const hasData = incomeMonths.length > 0 || expenseMonths.length > 0;

                const finalMonths = hasData ? allMonths : defaultMonths;
                const finalIncomeData = hasData ? finalMonths.map(month => incomes[month] ?? 0) : defaultIncomeData;
                const finalExpenseData = hasData ? finalMonths.map(month => expenses[month] ?? 0) : defaultExpenseData;

                const totalRevenueChartOptions = {
                    series: [
                        {name: 'Incomes', data: finalIncomeData},
                        {name: 'Expenses', data: finalExpenseData}
                    ],
                    chart: {
                        height: 300,
                        stacked: true,
                        type: 'bar',
                        toolbar: {show: false}
                    },
                    plotOptions: {
                        bar: {
                            horizontal: false,
                            columnWidth: '33%',
                            borderRadius: 12,
                            startingShape: 'rounded',
                            endingShape: 'rounded'
                        }
                    },
                    colors: [config.colors.primary, config.colors.info],
                    dataLabels: {enabled: false},
                    stroke: {
                        curve: 'smooth',
                        width: 6,
                        lineCap: 'round',
                        colors: [cardColor]
                    },
                    legend: {
                        show: true,
                        horizontalAlign: 'left',
                        position: 'top',
                        markers: {height: 8, width: 8, radius: 12, offsetX: -3},
                        labels: {colors: axisColor},
                        itemMargin: {horizontal: 10}
                    },
                    grid: {
                        borderColor: borderColor,
                        padding: {top: 0, bottom: -8, left: 20, right: 20}
                    },
                    xaxis: {
                        categories: finalMonths,
                        labels: {style: {fontSize: '13px', colors: axisColor}},
                        axisTicks: {show: false},
                        axisBorder: {show: false}
                    },
                    yaxis: {
                        labels: {style: {fontSize: '13px', colors: axisColor}}
                    },
                    responsive: [ /* your existing breakpoints */],
                    states: {
                        hover: {filter: {type: 'none'}},
                        active: {filter: {type: 'none'}}
                    }
                };

                const totalRevenueChart = new ApexCharts(totalRevenueChartEl, totalRevenueChartOptions);
                totalRevenueChart.render();
            })
            .catch(err => {
                console.error('❌ Error fetching income/expense diagram:', err);

                // On error — fallback to default data
                const totalRevenueChartOptions = {
                    series: [
                        {name: 'Incomes', data: [18, 7, 15, 29, 18, 12, 9]},
                        {name: 'Expenses', data: [-13, -18, -9, -14, -5, -17, -15]}
                    ],
                    chart: {
                        height: 300,
                        stacked: true,
                        type: 'bar',
                        toolbar: {show: false}
                    },
                    plotOptions: {
                        bar: {
                            horizontal: false,
                            columnWidth: '33%',
                            borderRadius: 12,
                            startingShape: 'rounded',
                            endingShape: 'rounded'
                        }
                    },
                    colors: [config.colors.primary, config.colors.info],
                    dataLabels: {enabled: false},
                    stroke: {
                        curve: 'smooth',
                        width: 6,
                        lineCap: 'round',
                        colors: [cardColor]
                    },
                    legend: {
                        show: true,
                        horizontalAlign: 'left',
                        position: 'top',
                        markers: {height: 8, width: 8, radius: 12, offsetX: -3},
                        labels: {colors: axisColor},
                        itemMargin: {horizontal: 10}
                    },
                    grid: {
                        borderColor: borderColor,
                        padding: {top: 0, bottom: -8, left: 20, right: 20}
                    },
                    xaxis: {
                        categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'],
                        labels: {style: {fontSize: '13px', colors: axisColor}},
                        axisTicks: {show: false},
                        axisBorder: {show: false}
                    },
                    yaxis: {
                        labels: {style: {fontSize: '13px', colors: axisColor}}
                    },
                    responsive: [ /* your existing breakpoints */],
                    states: {
                        hover: {filter: {type: 'none'}},
                        active: {filter: {type: 'none'}}
                    }
                };

                const totalRevenueChart = new ApexCharts(totalRevenueChartEl, totalRevenueChartOptions);
                totalRevenueChart.render();
            });
    }

    // Growth Chart - Radial Bar Chart
    // --------------------------------------------------------------------
    const growthChartEl = document.querySelector('#growthChart'),
        growthChartOptions = {
            series: [78],
            labels: ['Growth'],
            chart: {
                height: 240,
                type: 'radialBar'
            },
            plotOptions: {
                radialBar: {
                    size: 150,
                    offsetY: 10,
                    startAngle: -150,
                    endAngle: 150,
                    hollow: {
                        size: '55%'
                    },
                    track: {
                        background: cardColor,
                        strokeWidth: '100%'
                    },
                    dataLabels: {
                        name: {
                            offsetY: 15,
                            color: headingColor,
                            fontSize: '15px',
                            fontWeight: '600',
                            fontFamily: 'Public Sans'
                        },
                        value: {
                            offsetY: -25,
                            color: headingColor,
                            fontSize: '22px',
                            fontWeight: '500',
                            fontFamily: 'Public Sans'
                        }
                    }
                }
            },
            colors: [config.colors.primary],
            fill: {
                type: 'gradient',
                gradient: {
                    shade: 'dark',
                    shadeIntensity: 0.5,
                    gradientToColors: [config.colors.primary],
                    inverseColors: true,
                    opacityFrom: 1,
                    opacityTo: 0.6,
                    stops: [30, 70, 100]
                }
            },
            stroke: {
                dashArray: 5
            },
            grid: {
                padding: {
                    top: -35,
                    bottom: -10
                }
            },
            states: {
                hover: {
                    filter: {
                        type: 'none'
                    }
                },
                active: {
                    filter: {
                        type: 'none'
                    }
                }
            }
        };
    if (typeof growthChartEl !== undefined && growthChartEl !== null) {
        const growthChart = new ApexCharts(growthChartEl, growthChartOptions);
        growthChart.render();
    }

    // Profit Report Line Chart
    // --------------------------------------------------------------------
    const profileReportChartEl = document.querySelector('#profileReportChart'),
        profileReportChartConfig = {
            chart: {
                height: 80,
                // width: 175,
                type: 'line',
                toolbar: {
                    show: false
                },
                dropShadow: {
                    enabled: true,
                    top: 10,
                    left: 5,
                    blur: 3,
                    color: config.colors.warning,
                    opacity: 0.15
                },
                sparkline: {
                    enabled: true
                }
            },
            grid: {
                show: false,
                padding: {
                    right: 8
                }
            },
            colors: [config.colors.warning],
            dataLabels: {
                enabled: false
            },
            stroke: {
                width: 5,
                curve: 'smooth'
            },
            series: [
                {
                    data: [110, 270, 145, 245, 205, 285]
                }
            ],
            xaxis: {
                show: false,
                lines: {
                    show: false
                },
                labels: {
                    show: false
                },
                axisBorder: {
                    show: false
                }
            },
            yaxis: {
                show: false
            }
        };
    if (typeof profileReportChartEl !== undefined && profileReportChartEl !== null) {
        const profileReportChart = new ApexCharts(profileReportChartEl, profileReportChartConfig);
        profileReportChart.render();
    }

    // Order Statistics Chart
    // --------------------------------------------------------------------
// Order Statistics Chart - Dynamic fetch and fallback
// --------------------------------------------------------------------
    const chartOrderStatistics = document.querySelector('#orderStatisticsChart');

    if (chartOrderStatistics) {
        const urlParams = new URLSearchParams(window.location.search);
        const cardNumber = urlParams.get('cardNumber');

        let apiUrl = '/api/clients/statistics';
        if (cardNumber) {
            apiUrl += `?cardNumber=${encodeURIComponent(cardNumber)}`;
        }

        fetch(apiUrl)
            .then(response => response.json())
            .then(data => {
                const categoriesDiagram = data.categoriesDiagram || [];

                let labelsFull = categoriesDiagram.map(item => item.categoryName);
                let series = categoriesDiagram.map(item => parseFloat(Math.abs(item.sum).toFixed(0)));

// Fallback default data
                const defaultLabels = ['Electronic', 'Sports', 'Decor', 'Fashion'];
                const defaultSeries = [25, 15, 32, 28];

                if (labelsFull.length === 0 || series.length === 0) {
                    labelsFull = defaultLabels;
                    series = defaultSeries;
                }

                // Truncate labels to max 8 characters
                const labelsTruncated = labelsFull.map(label =>
                    label.length > 8 ? label.substring(0, 8) + '…' : label
                );

                const customColors = [
                    config.colors.primary, config.colors.secondary, config.colors.info, config.colors.success,
                    '#FFD93D', '#6BCB77', '#4D96FF', '#A0E7E5', '#FFC75F', '#C77DFF',
                    '#FF9671', '#845EC2', '#00C9A7', '#F9F871', '#D65DB1', '#0081CF',
                    '#FF8066', '#B39CD0', '#F6A6B2'
                ];

                const orderChartConfig = {
                    chart: {height: 165, width: 145, type: 'donut'},
                    labels: labelsTruncated,
                    series: series,
                    colors: customColors,
                    stroke: {width: 2},
                    dataLabels: {enabled: false},
                    legend: {show: false},
                    grid: {padding: {top: 0, bottom: 0, right: 15}},
                    tooltip: {
                        y: {
                            formatter: function (value, opts) {
                                const index = opts.dataPointIndex;
                                const fullLabel = labelsFull[index];
                                const total = opts.globals.seriesTotals.reduce((a, b) => a + b, 0);
                                const percent = ((value / total) * 100).toFixed(1); // one decimal place
                                return `${fullLabel}: ${value} (${percent}%)`;
                            }
                        }
                    },
                    plotOptions: {
                        pie: {
                            donut: {
                                size: '75%',
                                labels: {
                                    show: true,
                                    value: {
                                        fontSize: '1.5rem',
                                        color: headingColor,
                                        offsetY: -15,
                                        formatter: function (val) {
                                            return parseInt(val);
                                        }
                                    },
                                    name: {
                                        offsetY: 20
                                    },
                                    total: {
                                        show: true,
                                        label: 'Total',
                                        formatter: function (w) {
                                            const total = w.globals.seriesTotals.reduce((a, b) => a + b, 0);
                                            return total.toString();
                                        }
                                    }
                                }
                            }
                        }
                    }
                };

                const statisticsChart = new ApexCharts(chartOrderStatistics, orderChartConfig);
                statisticsChart.render();
            })
            .catch(error => {
                console.error('Error fetching chart data:', error);
            });


    }

    const incomeChartEl = document.querySelector('#incomeChart');

    if (incomeChartEl) {
        const urlParams = new URLSearchParams(window.location.search);
        const cardNumber = urlParams.get('cardNumber');

        let apiUrl = '/api/clients/statistics';
        if (cardNumber) {
            apiUrl += `?cardNumber=${encodeURIComponent(cardNumber)}`;
        }

        fetch(apiUrl)
            .then(response => response.json())
            .then(data => {
                const totalBalanceGraphic = data.totalBalanceGraphic || {};

                let categories = [];
                let seriesData = [];

                const defaultCategories = ['', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul'];
                const defaultSeriesData = [24, 21, 30, 22, 42, 26, 35, 29];

                if (Object.keys(totalBalanceGraphic).length === 0) {
                    categories = defaultCategories;
                    seriesData = defaultSeriesData;
                } else {
                    const monthOrder = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

                    const now = new Date();
                    const currentMonthIndex = now.getMonth();

                    const last6Months = [];
                    for (let i = 5; i >= 0; i--) {
                        const index = (currentMonthIndex - i + 12) % 12;
                        last6Months.push(monthOrder[index]);
                    }

                    last6Months.forEach(month => {
                        if (!(month in totalBalanceGraphic)) {
                            totalBalanceGraphic[month] = 0;
                        }
                    });

                    // Extract data
                    categories = last6Months;
                    seriesData = last6Months.map(month => totalBalanceGraphic[month]);

                    // Check if all values are 0
                    const allZero = seriesData.every(value => value === 0);

                    // Use default if all are zero
                    if (allZero) {
                        categories = defaultCategories;
                        seriesData = defaultSeriesData;
                    }
                }

                const incomeChartConfig = {
                    series: [{
                        data: seriesData
                    }],
                    chart: {
                        height: 215,
                        parentHeightOffset: 0,
                        parentWidthOffset: 0,
                        toolbar: {show: false},
                        type: 'area'
                    },
                    dataLabels: {enabled: false},
                    stroke: {width: 2, curve: 'smooth'},
                    legend: {show: false},
                    markers: {
                        size: 6,
                        colors: 'transparent',
                        strokeColors: 'transparent',
                        strokeWidth: 4,
                        discrete: [{
                            fillColor: config.colors.white,
                            seriesIndex: 0,
                            dataPointIndex: seriesData.length - 1,
                            strokeColor: config.colors.primary,
                            strokeWidth: 2,
                            size: 6,
                            radius: 8
                        }],
                        hover: {size: 7}
                    },
                    colors: [config.colors.primary],
                    fill: {
                        type: 'gradient',
                        gradient: {
                            shade: shadeColor,
                            shadeIntensity: 0.6,
                            opacityFrom: 0.5,
                            opacityTo: 0.25,
                            stops: [0, 95, 100]
                        }
                    },
                    grid: {
                        borderColor: borderColor,
                        strokeDashArray: 3,
                        padding: {top: -20, bottom: -8, left: -10, right: 8}
                    },
                    xaxis: {
                        categories: categories,
                        axisBorder: {show: false},
                        axisTicks: {show: false},
                        labels: {
                            show: true,
                            style: {fontSize: '13px', colors: axisColor}
                        }
                    },
                    yaxis: {
                        labels: {show: false},
                        tickAmount: 4
                    }
                };

                const incomeChart = new ApexCharts(incomeChartEl, incomeChartConfig);
                incomeChart.render();
            })
            .catch(error => {
                console.error('Error fetching dashboard statistics:', error);
            });
    }

    if (typeof incomeChartEl !== undefined && incomeChartEl !== null) {
        const incomeChart = new ApexCharts(incomeChartEl, incomeChartConfig);
        incomeChart.render();
    }

    // Expenses Mini Chart - Radial Chart
    // --------------------------------------------------------------------
    const weeklyExpensesEl = document.querySelector('#expensesOfWeek'),
        weeklyExpensesConfig = {
            series: [65],
            chart: {
                width: 60,
                height: 60,
                type: 'radialBar'
            },
            plotOptions: {
                radialBar: {
                    startAngle: 0,
                    endAngle: 360,
                    strokeWidth: '8',
                    hollow: {
                        margin: 2,
                        size: '45%'
                    },
                    track: {
                        strokeWidth: '50%',
                        background: borderColor
                    },
                    dataLabels: {
                        show: true,
                        name: {
                            show: false
                        },
                        value: {
                            formatter: function (val) {
                                return '$' + parseInt(val);
                            },
                            offsetY: 5,
                            color: '#697a8d',
                            fontSize: '13px',
                            show: true
                        }
                    }
                }
            },
            fill: {
                type: 'solid',
                colors: config.colors.primary
            },
            stroke: {
                lineCap: 'round'
            },
            grid: {
                padding: {
                    top: -10,
                    bottom: -15,
                    left: -10,
                    right: -10
                }
            },
            states: {
                hover: {
                    filter: {
                        type: 'none'
                    }
                },
                active: {
                    filter: {
                        type: 'none'
                    }
                }
            }
        };
    if (typeof weeklyExpensesEl !== undefined && weeklyExpensesEl !== null) {
        const weeklyExpenses = new ApexCharts(weeklyExpensesEl, weeklyExpensesConfig);
        weeklyExpenses.render();
    }
})();
